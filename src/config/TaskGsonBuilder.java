package config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;


public class TaskGsonBuilder {

   public static Gson getGson() {
       return new GsonBuilder()
               //   .setPrettyPrinting()
               .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
               .registerTypeAdapter(Duration.class,new DurationAdapter())
               .create();
    }
}
