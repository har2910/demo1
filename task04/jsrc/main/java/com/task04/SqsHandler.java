package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "sqs_handler",
	roleName = "sqs_handler-role",
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)

public class SqsHandler implements RequestHandler<SQSEvent, String> {

	@Override
	public String handleRequest(SQSEvent event, Context context) {
		// Loop through each record in the SQS event
		for (SQSEvent.SQSMessage message : event.getRecords()) {
			// Log the message content
			context.getLogger().log("Received SQS message: " + message.getBody());
		}
//		return "SQS message logged successfully!";
		JSONObject response = new JSONObject();
		response.put("statusCode", 200);
		response.put("body", "SNS message logged successfully");
		return response.toString();
	}
}
