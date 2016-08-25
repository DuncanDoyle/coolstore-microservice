/**
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.coolstore.api_gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.redhat.coolstore.api_gateway.model.Inventory;
import com.redhat.coolstore.api_gateway.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.redhat.coolstore.api_gateway.feign.FeignClientFactory;

import io.swagger.annotations.ApiOperation;

import javax.json.*;

@RestController
@RequestMapping("api")
public class ApiGatewayController {

    @Autowired
    private FeignClientFactory feignClientFactory;

    /**
     * This /api REST endpoint uses Java 8 parallel stream to create the Feign, invoke it, and collect the result as a List that
     * will be rendered as a JSON Array.
     *
     * @return
     */

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, value = "/products/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation("Get a list of products")
    public List<Product> list() throws ExecutionException, InterruptedException {

        final CompletableFuture<List<Product>> productList = CompletableFuture.supplyAsync(() ->
                feignClientFactory.getPricingClient().getService().list());

        final CompletableFuture<List<Inventory>> inventoryList = CompletableFuture.supplyAsync(() ->
                feignClientFactory.getInventoryClient().getService().list());

        return productList.thenCombine(inventoryList, (products, inventory) -> {

            System.out.println("Products result: " + products);
            System.out.println("Inventory result: " + inventory);

            // create inventory map
            Map<String, String> iMap = new HashMap<>();
            inventory.forEach(i -> iMap.put(i.itemId, i.availability));

            products.forEach(p -> p.availability = iMap.get(p.itemId));

            return products;

        }).get();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/health")
    @ApiOperation("Used to verify the health of the service")
    public String health() {
        return "I'm ok";
    }
}