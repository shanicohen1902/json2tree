import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class jsonTreeParserMain {
	final String jsonFile = "node_to_json.json";

	public static void main(String[] args) throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File("./src/main/java/node_to_json.json"));
        
        // a.Count the number of objects in the tree.
        System.out.println("Number of nodes: "+ countNodes(root));
        // b.Find how many objects are under Dan Dan?
        System.out.println("Number of nodes under Dan Dan: "+ countNodesUnderNode(root,"Dan Dan","name"));
        // c.Count how many different job titles we have in the 
        // JSON and how many people are from each job title.
        countNodesFieldsFrequency(root,"title");        
	}	
	  
	private static int countNodes(JsonNode node) {
		  int nodes=1;
		  if(node == null)return -1;
	      if (node.getNodeType() == JsonNodeType.ARRAY) {	    	  
		      for (JsonNode jsonArrayNode : node) {
		        	  nodes+=countNodes(jsonArrayNode);
		      }	    	  
	      } 
	      if (node.getNodeType() == JsonNodeType.OBJECT) {		  
		      JsonNode children = getChildren(node);
		      if (children!=null) {
		    	  nodes=countNodes(children);
	          }	  	      
	      }
	      return nodes;	      
	  }	 
	  
	  private static int countNodesUnderNode(JsonNode root, String name, String fieldName) {
		return countNodesUnderNode(root,name,fieldName,false) - 1;
	  }	
	
	  private static int countNodesUnderNode(JsonNode node, String name, String fieldName, boolean count) {
		  if(node == null)return -1;
		  int nodes=count?1:0;
		  
	      if (node.getNodeType() == JsonNodeType.ARRAY) {	    	  
		      for (JsonNode jsonArrayNode : node) {
		        	  nodes+= countNodesUnderNode(jsonArrayNode,name,fieldName,count);
		      }	    	  
	      } 	      
	      if (node.getNodeType() == JsonNodeType.OBJECT) {		    	 
		      if(count == false) {
		    	  count=name.equals(node.get(fieldName).asText());
		      }		      
		      JsonNode children = getChildren(node);
		      if (children!=null) {
		    	  nodes= countNodesUnderNode(children,name, fieldName,count);
	          }	  	      
	      }	      	      
	      return nodes;
	  }	 

		private static void countNodesFieldsFrequency(JsonNode root,String field) {
			  if(root == null) return ;
			  Map<String,Integer> results=countNodesFieldsFrequencyHelper(root,new HashMap<String,Integer>(), field);
			  System.out.println("Number of different jobs: "+results.keySet().size());  
			  System.out.println("Frequencies for each job: ");
			  results.entrySet().forEach(entry->{
				    System.out.println(entry.getKey() + "-> " + entry.getValue());  
			  });
		}

		private static HashMap<String, Integer> countNodesFieldsFrequencyHelper(JsonNode node, HashMap<String, Integer> hashMap,String field) {
			  if(node == null)return hashMap;
		      if (node.getNodeType() == JsonNodeType.ARRAY) {	    	  
			      for (JsonNode jsonArrayNode : node) {
			        	  countNodesFieldsFrequencyHelper(jsonArrayNode,hashMap,field);
			      }	    	  
		      } 
		      if (node.getNodeType() == JsonNodeType.OBJECT ) {	
		    	  if(node.get(field) != null) {
		    		  String value=node.get(field).asText();
			    	  int count = hashMap.containsKey(value) ? hashMap.get(value) : 0;
			    	  hashMap.put(value, count + 1);		    		  
		    	  }	    		      	  
		    	  
	        	countNodesFieldsFrequencyHelper(getChildren(node),hashMap,field);

			   } 	      
		      return hashMap;
		}

		private static JsonNode getChildren(JsonNode node) {
    	    Iterator<Entry<String, JsonNode>> fields = node.fields();
    	    while (fields.hasNext()) {
    	        Entry<String, JsonNode> jsonField = fields.next();
    	        if(jsonField.getValue().getNodeType() == JsonNodeType.ARRAY) {
    	        	return jsonField.getValue();
    	        }
    	   	}		    	     	  	    	  			
			return null;
		}
	  
}