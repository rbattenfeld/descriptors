package org.jboss.shrinkwrap.descriptor.test.webapp25;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor;
import org.junit.Test;

public class WebAppDescriptorExtTestCase {

    private final Logger log = Logger.getLogger(WebAppDescriptorExtTestCase.class.getName());

    @Test
    public void testFilterTypeExt() throws Exception {
    	final WebAppDescriptor webAppDescr = create();
    	webAppDescr.factory().filterTypeExtWebapp25().filter("myFilterName", "com.myfilter.filter", new String[] {"url1", "url2"});
    	webAppDescr.addFilter(webAppDescr
    			.factory()
    			.filterTypeExtWebapp25()
    			.filter("myFilterName", "com.myfilter.filter", new String[] {"url1", "url2"}));
    	
//    	log.info("web.xml after update: " + webAppDescr.exportAsString());
    	    	
    	assertTrue(webAppDescr.getAllFilter().size() == 1);
    	assertTrue(webAppDescr.getAllFilterMapping().size() == 1);
    	assertTrue(webAppDescr.getAllFilterMapping().get(0).getAllUrlPattern().size() == 2);    	
    	assertEquals(webAppDescr.getAllFilter().get(0).getFilterName(), "myFilterName");
    	assertEquals(webAppDescr.getAllFilter().get(0).getFilterClass(), "com.myfilter.filter"); 
    	assertEquals(webAppDescr.getAllFilterMapping().get(0).getFilterName(), "myFilterName");
    	assertEquals(webAppDescr.getAllFilterMapping().get(0).getAllUrlPattern().get(0), "url1");
    	assertEquals(webAppDescr.getAllFilterMapping().get(0).getAllUrlPattern().get(1), "url2");
    }
    
    // -------------------------------------------------------------------------------------||
    // Helper Methods ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private WebAppDescriptor create() {
        return Descriptors.create(WebAppDescriptor.class);
    }
}
