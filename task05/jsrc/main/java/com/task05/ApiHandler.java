package com.task05;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@LambdaHandler(
    lambdaName = "api_handler",
	roleName = "api_handler-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
//@EnvironmentVariables(value = {
//		@EnvironmentVariable(key = "target_table", value = "Events")
//})
//private final String tableName = "Events";


class EventRequest {
	public int principalId;
	public Map<String, String> content;
}

class EventResponse {
	public int statusCode;
	public Map<String, Object> event;

	public EventResponse(int statusCode, Map<String, Object> event) {
		this.statusCode = statusCode;
		this.event = event;
	}
}

public class ApiHandler implements RequestHandler<EventRequest, EventResponse> {

	private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
	private final DynamoDB dynamoDB = new DynamoDB(client);
//	private final String tableName = System.getenv("target_table");
private final String tableName = "Events";


	@Override
	public EventResponse handleRequest(EventRequest request, Context context) {
		// Log environment variable to verify it
		context.getLogger().log("Target Table: " + tableName);

		// Generate unique ID and timestamp in ISO 8601 format
		String eventId = UUID.randomUUID().toString();
		String createdAt = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

		// Prepare item to insert into DynamoDB
		Map<String, Object> itemMap = new HashMap<>();
		itemMap.put("id", eventId);
		itemMap.put("principalId", request.principalId);
		itemMap.put("createdAt", createdAt);
		itemMap.put("body", request.content);

		// Save item to DynamoDB
		Table table = dynamoDB.getTable(tableName);
		try {
			Item item = new Item()
					.withPrimaryKey("id", eventId)
					.withNumber("principalId", request.principalId)
					.withString("createdAt", createdAt)
					.withMap("body", request.content);
			table.putItem(item);
		} catch (Exception e) {
			context.getLogger().log("Error saving to DynamoDB: " + e.getMessage());
			return new EventResponse(500, Map.of("error", "Internal server error"));
		}

		// Return response with status code and created event details
		context.getLogger().log("Item successfully saved to DynamoDB with ID: " + eventId);
		return new EventResponse(201, itemMap);
	}
}


//@EnvironmentVariables(value = {
//		@EnvironmentVariable(key = "target_table", value = "${target_table}")
//})
//public class ApiHandler implements RequestHandler<RequestData, Response> {
//
//	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
//	private DynamoDB dynamoDb = new DynamoDB(client);
//	private String DYNAMODB_TABLE_NAME = System.getenv("target_table");
//
//	@Override
//	public Response handleRequest(RequestData event1, Context context) {
//
//		int principalId = event1.getPrincipalId();
//		Map<String, String> content = event1.getContent();
//
//		String newId = UUID.randomUUID().toString();
//		String currentTime = DateTimeFormatter.ISO_INSTANT
//				.format(Instant.now().atOffset(ZoneOffset.UTC));
//
//		Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME);
//
//		Item item = new Item()
//				.withPrimaryKey("id", newId)
//				.withInt("principalId", principalId)
//				.withString("createdAt", currentTime)
//				.withMap("body", content);
//
//		table.putItem(item);
//
//		Event event = Event.builder()
//				.id(newId)
//				.principalId(principalId)
//				.createdAt(currentTime)
//				.body(content)
//				.build();
//
//		Response response = Response.builder()
//				.statusCode(201)
//				.event(event)
//				.build();
//
//		return response;
//
//	}
//}
