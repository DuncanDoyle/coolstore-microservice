package com.redhat.coolstore.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import com.redhat.coolstore.model.Promotion;
import com.redhat.coolstore.model.ShoppingCart;
import com.redhat.coolstore.model.ShoppingCartItem;

@ApplicationScoped
public class PromoService implements Serializable {

	private static final long serialVersionUID = 2088590587856645568L;

	private String name = null;
	
	private Set<Promotion> PromotionSet = null;

	public PromoService() {
						
		PromotionSet = new HashSet<Promotion>();
		
		PromotionSet.add(new Promotion("329299", .25));
						
	}
			
	public void applyCartItemPromotions(ShoppingCart shoppingCart) {
		
		if ( shoppingCart != null && shoppingCart.getShoppingCartItemList().size() > 0 ) {
			
			Map<String, Promotion> promoMap = new HashMap<String, Promotion>(); 
			
			for (Promotion promo : getPromotions()) {
				
				promoMap.put(promo.getItemId(), promo);
				
			}
			
			for ( ShoppingCartItem sci : shoppingCart.getShoppingCartItemList() ) {
				
				String productId = sci.getProduct().getItemId();
				
				Promotion promo = promoMap.get(productId);
				
				if ( promo != null ) {
				
					sci.setPromoSavings(sci.getProduct().getPrice() * promo.getPercentOff() * -1);
					sci.setPrice(sci.getProduct().getPrice() * (1-promo.getPercentOff()));
					
				}
							
			}
			
		}
		
	}
	
	public void applyShippingPromotions(ShoppingCart shoppingCart) {
		
		if ( shoppingCart != null ) {
			
			//PROMO: if cart total is greater than 75, free shipping
			if ( shoppingCart.getCartItemTotal() >= 75) {
				
				shoppingCart.setShippingPromoSavings(shoppingCart.getShippingTotal() * -1);
				shoppingCart.setShippingTotal(0);
				
			}
			
		}
		
	}	
		
	public Set<Promotion> getPromotions() {
				
		if ( PromotionSet == null ) {
			
			PromotionSet = new HashSet<Promotion>();
			
		}
		
		return new HashSet<Promotion>(PromotionSet);
		
	}
	
	public void setPromotions(Set<Promotion> PromotionSet) {
		
		if ( PromotionSet != null ) {
		
			this.PromotionSet = new HashSet<Promotion>(PromotionSet);
			
		} else {
			
			this.PromotionSet = new HashSet<Promotion>();
			
		}
						
	}
	
	@Override
	public String toString() {
		return "PromoService [name=" + name + ", PromotionSet=" + PromotionSet
				+ "]";
	}
	
}
