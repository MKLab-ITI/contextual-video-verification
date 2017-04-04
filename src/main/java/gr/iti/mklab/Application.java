package gr.iti.mklab;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;

/**
 * Context Aggregation and Analysis service 
 * @author olgapapa
 * 
 */

@SpringBootApplication
public class Application {

	@Value("${pool.size:1}")
	private int poolSize;;

	@Value("${queue.capacity:0}")
	private int queueCapacity;

	@Bean(name="workExecutor")
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(poolSize);
		taskExecutor.setQueueCapacity(queueCapacity);
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
                // Customize the default entries in errorAttributes to suit your needs
                System.out.println("find status " + errorAttributes.get("status"));
                if (errorAttributes.get("status").toString().equalsIgnoreCase("404")){
                	System.out.println("ERROR 404 custom");
                	errorAttributes.replace("message", "The requested resource could not be found. "
                			+ "Existing methods /verify_video, /get_ytverification, /get_twverification, /weather");
                }
                errorAttributes.put("contact", "olgapapa@iti.gr");
                return errorAttributes;
            }
       };
    }

}
