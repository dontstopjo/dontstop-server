package dontstopjo.ootdrop.global.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cloud.aws.s3")
data class AwsS3Properties(
    val bucket: String,
    val region: String,
    val accessKey: String = "",
    val secretKey: String = "",
    val endpoint: String = ""
)
