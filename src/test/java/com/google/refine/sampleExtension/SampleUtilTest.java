package com.google.refine.sampleExtension;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import org.testng.annotations.Test;

public class SampleUtilTest {
    
    @Test
    public void testStringArrayLength() {
        String[] myArray = new String[] { "foo", "bar" };
        
        assertEquals(SampleUtil.stringArrayLength(myArray), 2);
    }
    
    @Test
    public void testNullPointerException() {
        assertThrows(NullPointerException.class, () -> SampleUtil.stringArrayLength(null));
    }

}
