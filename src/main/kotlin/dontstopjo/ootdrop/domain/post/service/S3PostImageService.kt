package dontstopjo.ootdrop.domain.post.service

import dontstopjo.ootdrop.global.config.AwsS3Properties
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.UUID

data class UploadedImage(
    val imageUrl: String,
    val imageKey: String
)

@Service
class S3PostImageService(
    private val s3Client: S3Client,
    private val awsS3Properties: AwsS3Properties
) {
    fun uploadPostImage(postId: Long, id: String, file: MultipartFile): UploadedImage {
        validateImage(file)

        val extension = file.originalFilename
            ?.substringAfterLast('.', "")
            ?.takeIf { it.isNotBlank() }
            ?.lowercase()
        val fileName = if (extension != null) "${UUID.randomUUID()}.$extension" else UUID.randomUUID().toString()
        val key = "posts/$postId/$id/$fileName"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(awsS3Properties.bucket)
            .key(key)
            .contentType(file.contentType ?: "application/octet-stream")
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.bytes))

        return UploadedImage(
            imageUrl = buildImageUrl(key),
            imageKey = key
        )
    }

    fun deleteByKey(imageKey: String) {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(awsS3Properties.bucket)
            .key(imageKey)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }

    private fun validateImage(file: MultipartFile) {
        if (file.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "image file is empty")
        }

        val contentType = file.contentType ?: ""
        if (!contentType.startsWith("image/")) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "only image files are allowed")
        }
    }

    private fun buildImageUrl(key: String): String {
        if (awsS3Properties.endpoint.isNotBlank()) {
            return "${awsS3Properties.endpoint.trimEnd('/')}/${awsS3Properties.bucket}/$key"
        }

        return "https://${awsS3Properties.bucket}.s3.${awsS3Properties.region}.amazonaws.com/$key"
    }
}
