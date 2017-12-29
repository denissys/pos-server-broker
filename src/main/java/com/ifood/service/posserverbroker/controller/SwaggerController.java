package com.ifood.service.posserverbroker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@ApiIgnore
@RestController
@RequestMapping("/")
public class SwaggerController {

	@RequestMapping(method = GET)
	public ModelAndView index() {
		return new ModelAndView("redirect:swagger-ui.html");
	}

}
