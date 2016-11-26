package org.test.redisson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
//import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.config.Config;


public class Application {
    
    public static BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( System.in ) );
    
    public static String strHashMapName = null;

    public static String strTopicName = null;
    
    public static void printMenuOptions() {
        
        System.out.println( " " );
        System.out.println( "*---------------------------------------------*" );
        System.out.println( "| 0 - Create hashmap                          |" );
        System.out.println( "| 1 - Add entry key and value                 |" );
        System.out.println( "| 2 - Delete entry key                        |" );
        System.out.println( "| 3 - Print hashmap content                   |" );
        System.out.println( "| 4 - Create topic                            |" );
        System.out.println( "| 5 - Send message over topic                 |" );
        System.out.println( "| 9 - Exit                                    |" );
        System.out.println( "*---------------------------------------------*" );
        System.out.println( "Working hashmap name: " + strHashMapName );
        System.out.println( "Working topic name: " + strTopicName );
        System.out.print( "Option: " );
        
    }
    
    public static String readEntry() {
        
        String strResult = "";
                
        try {
            
            strResult = bufferedReader.readLine();
        
        }
        catch ( IOException e ) {
        
            e.printStackTrace();
            
        }
        
        return strResult;
        
    }
    
    //@SuppressWarnings( "unused" )
    public static void main( String[] args ) {

        //Console c = System.console();
        
        Config config = new Config();
        //config.useClusterServers().addNodeAddress( "192.168.2.104:6379" );
        config.useSingleServer().setAddress( "192.168.2.104:6379" ).setPassword( "12345678" );
        
        // connects to 127.0.0.1:6379 by default
        RedissonClient redisson = Redisson.create( config );
       
        RMapCache<String, String> sharedMap = null;
        
        RTopic<String> sharedTopic = null;
        
        /*
        RMap<String, Integer> map = redisson.getMap( "myMap" );
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        
        boolean contains = map.containsKey("a");
        
        Integer value = map.get("c");
        Integer updatedValue = map.addAndGet("a", 32);
        
        //Integer valueSize = map.valueSize("c");
        
        Set<String> keys = new HashSet<String>();
        keys.add("a");
        keys.add("b");
        keys.add("c");
        Map<String, Integer> mapSlice = map.getAll(keys);
        
        // use read* methods to fetch all objects
        Set<String> allKeys = map.readAllKeySet();
        Collection<Integer> allValues = map.readAllValues();
        Set<Entry<String, Integer>> allEntries = map.readAllEntrySet();
        
        // use fast* methods when previous value is not required
        boolean isNewKey = map.fastPut("a", 100);
        boolean isNewKeyPut = map.fastPutIfAbsent("d", 33);
        long removedAmount = map.fastRemove("b");
        */
        
        while ( true ) {
            
            String strOption = "";
            
            /*try {
            
                //System.out.println( "Exit? y/n" );
                
                option = bufferedReader.readLine();
            
            }
            catch ( IOException e ) {
            
                e.printStackTrace();
                
            }*/
            
            printMenuOptions();
            strOption = readEntry();
            
            if ( strOption.equalsIgnoreCase( "0" )&& sharedMap == null ) {
                
                System.out.println( "Enter the hashmap name [test.redis.hashmap]: " );
                
                strHashMapName = readEntry();
                
                if ( strHashMapName == null || strHashMapName.equals( "" ) ) {
                    
                    strHashMapName = "test.redis.hashmap";
                    
                }
                
                sharedMap = redisson.getMapCache( strHashMapName );
                
                //sharedMap.expire( 1, TimeUnit.MINUTES );
                
            }
            else if ( strOption.equalsIgnoreCase( "1" ) && sharedMap != null ) {
                
                System.out.println( "Enter the hashmap key to add: " );
                
                final String strHashMapKey = readEntry();
                
                System.out.println( "Enter the hashmap value to add: " );
                
                final String strHashMapValue = readEntry();
                
                sharedMap.put( strHashMapKey, strHashMapValue, 1, TimeUnit.MINUTES );
                
            }
            else if ( strOption.equalsIgnoreCase( "2" ) && sharedMap != null ) {
                
                System.out.println( "Enter the hashmap key to delete: " );
                
                final String strHashMapKey = readEntry();
                
                sharedMap.remove( strHashMapKey );
                
            }
            else if ( strOption.equalsIgnoreCase( "3" ) && sharedMap != null ) {

                Set< Entry<String, String> > allEntries = sharedMap.readAllEntrySet();
                
                for ( Entry<String,String> entry : allEntries ) {
                    
                    System.out.println( entry.getKey() + " -> " + entry.getValue() );
                    
                }                

                System.out.println( "Press enter key to continue" );
                
                readEntry();
                
            }
            else if ( strOption.equalsIgnoreCase( "4" ) && sharedTopic == null ) {

                System.out.println( "Enter the topic name [test.redis.topic]: " );
                
                strTopicName = readEntry();
                
                if ( strTopicName == null || strTopicName.equals( "" ) ) {
                    
                    strTopicName = "test.redis.topic";
                    
                }
                
                sharedTopic = redisson.getTopic( strTopicName );
                
                sharedTopic.addListener(new MessageListener<String>() {

                    public void onMessage( String strChannel, String strMessage ) {
                    
                      System.out.println( "Message channel -> " + strChannel + " message -> " + strMessage );
                        
                    }
                    
                 });
                
            }
            else if ( strOption.equalsIgnoreCase( "5" ) && sharedTopic != null ) {
                
                System.out.println( "Enter the message: " );
                
                final String strMessage = readEntry();
                
                sharedTopic.publish( strMessage );
                
            }
            else if ( strOption.equalsIgnoreCase( "9" ) ) {
             
                break;
            
            }    
                
        }
        
        redisson.shutdown();
        
    }
    
}
