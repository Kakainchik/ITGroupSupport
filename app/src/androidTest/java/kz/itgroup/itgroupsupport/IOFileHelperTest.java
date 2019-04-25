package kz.itgroup.itgroupsupport;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
public class IOFileHelperTest {

    @Test
    public void contains_existingFile_true() {

        Context appContext = InstrumentationRegistry.getTargetContext();
        boolean actual = IOFileHelper.contains(appContext, "25042019131033");

        assertTrue(actual);
    }

    @Test
    public void contains_nonexistentFile_false() {

        Context appContext = InstrumentationRegistry.getTargetContext();
        boolean actual = IOFileHelper.contains(appContext, "2504200015917");

        assertFalse(actual);
    }

    @Test
    public void saveTokenFile_theSameFile() {

        Context appContext = InstrumentationRegistry.getTargetContext();
    }
}