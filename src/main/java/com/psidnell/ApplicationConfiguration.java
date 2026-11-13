package com.psidnell;

import com.psidnell.solution.AdaptiveSolutionEvaluator;
import com.psidnell.words.Dictionary;
import com.psidnell.words.WordList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

@Configuration
//@ComponentScan("com.psidnell")
@PropertySource("classpath:/com/psidnell/settings.properties")
public class ApplicationConfiguration {

    @Bean
    Wordle wordle () {
        return new Wordle();
    }

    @Bean
    WordList dictionary () throws IOException {
        return new Dictionary();
    }

    @Bean
    AdaptiveSolutionEvaluator strategy() {
        return new AdaptiveSolutionEvaluator();
    }

    @Bean
    public Main main() {
        return new Main();
    }
}
