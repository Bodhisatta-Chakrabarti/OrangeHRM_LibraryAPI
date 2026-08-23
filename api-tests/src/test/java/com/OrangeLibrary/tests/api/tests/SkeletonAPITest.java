package com.OrangeLibrary.tests.api.tests;

import com.OrangeLibrary.framework.core.config.ConfigManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SkeletonAPITest {

    @Test
    public void skeletonPlaceholderTest()
    {
        Assert.assertTrue(true);
//        System.out.println("API Base URL: " + ConfigManager.getProperty("api", "api.base.url"));
//        System.out.println("API Retry Count: " + ConfigManager.getProperty("api", "api.retry.count"));
    }

}
