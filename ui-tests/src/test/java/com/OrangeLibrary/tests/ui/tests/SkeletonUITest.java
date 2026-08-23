package com.OrangeLibrary.tests.ui.tests;

import com.OrangeLibrary.framework.core.config.ConfigManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SkeletonUITest {

    @Test
    public void skeletonPlaceholderTest()
    {
        Assert.assertTrue(true);
//        System.out.println("Base URL: " + ConfigManager.getProperty("ui", "base.url"));
//        System.out.println("Browser: " + ConfigManager.getProperty("ui", "browser"));
    }

}
