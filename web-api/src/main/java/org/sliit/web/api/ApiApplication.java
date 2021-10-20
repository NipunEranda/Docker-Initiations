package org.sliit.web.api;

import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
	    tags = {
	            @Tag(name="widget", description="Widget operations."),
	            @Tag(name="gasket", description="Operations related to gaskets")
	    },
	    info = @Info(
	        title="Web API",
	        version = "1.0.0",
	        contact = @Contact(
	            name = "Web API Support",
	            url = "https://www.sliit.lk/",
	            email = "IT18009446@my.sliit.lk"),
	        license = @License(
	            name = "Apache 2.0",
	            url = "https://www.apache.org/licenses/LICENSE-2.0.html"))
	)
public class ApiApplication extends Application{
	
}
