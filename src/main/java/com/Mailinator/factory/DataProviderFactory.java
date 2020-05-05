package com.Mailinator.factory;

import com.Mailinator.dataProvider.RandomDataProvider;

public class DataProviderFactory {
	
	public static RandomDataProvider getRandomDataProperty() {

		return new RandomDataProvider();
	}

}
