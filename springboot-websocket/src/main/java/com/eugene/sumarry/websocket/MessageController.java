package com.eugene.sumarry.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

@Controller
public class MessageController {


	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public String message(String message) throws Exception {
		return HtmlUtils.htmlEscape(message) + "!";
	}

}
