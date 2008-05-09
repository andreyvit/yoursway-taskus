package com.kalugin.plugins.sync.api.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.kalugin.plugins.sync.api.tests.comparator.AllComparatorTests;
import com.kalugin.plugins.sync.api.tests.synchronizer.AllSynchronizerTests;

@RunWith(Suite.class)
@SuiteClasses( { MiscTests.class, AllComparatorTests.class, AllSynchronizerTests.class })
public class AllTests {
    
}
