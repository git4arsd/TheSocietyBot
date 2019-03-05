package nationwide.co.uk.thesocietybot;

import com.ibm.watson.developer_cloud.assistant.v1.Assistant;
import com.ibm.watson.developer_cloud.assistant.v1.model.Context;
import com.ibm.watson.developer_cloud.assistant.v1.model.InputData;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageResponse;

public class WatsonAssistant {

    private static final String USERID = "";
    private static final String PASSWORD = "";
    private static final String WORKSPACEID = "";
    private ApiService apiService = new ApiService();
    private MessageResponse response;
    private String custID;


    public MessageContent getWatsonResponse(String input, Context context){

        MessageOptions options;
        // Set up Assistant service.
        Assistant service = new Assistant("2018-02-16");
        service.setUsernameAndPassword(USERID, // replace with service username
                PASSWORD); // replace with service password
        String workspaceId = WORKSPACEID; // replace with workspace ID

        InputData inputData = new InputData.Builder(input).build();

        if (context != null)
            options = new MessageOptions.Builder(workspaceId).input(inputData).context(context).build();
        else
            options = new MessageOptions.Builder(workspaceId).input(inputData).build();
        try {
            response = service.message(options).execute();
        }
        catch (Exception e){
            MessageContent messageContent = new MessageContent();
            messageContent.setMessage("Watson service is currently not available, please text us after some time");
            messageContent.setUserId("received");
            messageContent.setContext(context);

            return messageContent;
        }
        String responsetext = response.getOutput().getText().get(0);

        if ((response.getContext().get("custID"))!=null)
            custID = response.getContext().get("custID").toString();

        if (responsetext.contains("Invoke <")) {
            if (responsetext.contains("getAccbalance")){
                responsetext = apiService.getCustomerAccountBalances(custID);
            }
            else if(responsetext.contains("getAccProducts")){
                responsetext = apiService.getCustomerProducts(custID);
            }
            else if(responsetext.contains("getAccTransactions")){
                responsetext = apiService.getCustomerLasttransaction(custID);
            }
        }
        MessageContent messageContent = new MessageContent();
        messageContent.setMessage(responsetext);
        messageContent.setUserId("received");
        messageContent.setContext(response.getContext());

        return messageContent;
    }
}
