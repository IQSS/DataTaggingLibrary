# API documentation

The DataTags API connects DataTags (the tagging server) with Dataverse (repository for data).

Methods for each component are listed below.


## Tagging server:

1. requestInterview(repositoryName: String, callbackURL: String)
		* Dataverse makes this request and provides a callback URL for the tagging server to send its tags at the end.

2. postBackTo and unacceptableDataset(reason: String)
		* Tagging server calls one these methods when the interview is complete and the user wishes to return to Dataverse.
		  Both methods use the callbackURL provided in tagging-server(1) to send the tags. They then redirect the user to a URL sent
		  in Dataverse(1)



## Dataverse:

1. receiveTags(JsonObject tags, String sessionId)
		* Tagging server calls this method in tagging-server(2) to send the tags to Dataverse. Dataverse responds
		  with the URL of the session the user was in before they left on the requestInterview (tagging-server(1))
		  call.



To see the connection in action, both Dataverse and DataTags must be running locally on your machine. Go to the temporary form at

		localhost:8080/datatags-api-test.xhtml

and follow the instructions on screen. This will take you through Dataverse, into DataTags, and back out into Dataverse.
