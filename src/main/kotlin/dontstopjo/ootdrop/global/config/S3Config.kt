package dontstopjo.ootdrop.global.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
@EnableConfigurationProperties(AwsS3Properties::class)
class S3Config(
    private val awsS3Properties: AwsS3Properties
) {
    @Bean
    fun s3Client(): S3Client {
        val builder = S3Client.builder()
            .region(Region.of(awsS3Properties.region))

        if (awsS3Properties.accessKey.isNotBlank() && awsS3Properties.secretKey.isNotBlank()) {
            val credentials = AwsBasicCredentials.create(awsS3Properties.accessKey, awsS3Properties.secretKey)
            builder.credentialsProvider(StaticCredentialsProvider.create(credentials))
        }

        if (awsS3Properties.endpoint.isNotBlank()) {
            builder.endpointOverride(URI.create(awsS3Properties.endpoint))
        }

        return builder.build()
    }
}
