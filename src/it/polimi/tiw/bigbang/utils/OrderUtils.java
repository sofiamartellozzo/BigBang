package it.polimi.tiw.bigbang.utils;

import java.util.List;

import it.polimi.tiw.bigbang.beans.SelectedItem;
import it.polimi.tiw.bigbang.beans.ShippingRange;
import it.polimi.tiw.bigbang.beans.Vendor;

public class OrderUtils {
	
	public static float calculateShipping(Vendor vendor, List<SelectedItem> items) {
		float shippingPrice = 0;

		int numberOfItems = 0;
		for (SelectedItem s : items) {
			numberOfItems = numberOfItems + s.getQuantity();
		}

		float total = 0;
		for (SelectedItem s : items) {
			total = total + (s.getCost() * s.getQuantity());
		}
//se free limit è posto a null?
		
		if (total >= vendor.getFree_limit()) {
			shippingPrice = 0;
		} else {
			for (ShippingRange s : vendor.getRanges()) {
				if ((s.getMin() <= numberOfItems) && (s.getMax() >= numberOfItems)) {
					shippingPrice = s.getCost();
				}
			}
			total=total+shippingPrice;
		}
		return shippingPrice;
	}

}
