package dontstopjo.ootdrop.global.s3

import dontstopjo.ootdrop.global.config.AwsS3Properties
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

@Service
class S3Service(
    private val s3Client: S3Client,
    private val awsS3Properties: AwsS3Properties
) {
    /**
     * 파일을 S3에 업로드하고 파일 키를 반환합니다.
     * @param dirName 디렉토리 이름 (e.g., "profiles", "posts")
     * @param file 업로드할 파일
     * @return S3에 저장된 파일의 키
     */
    fun uploadFile(dirName: String, file: MultipartFile): String {
        validateImage(file)

        val extension = file.originalFilename
            ?.substringAfterLast('.', "")
            ?.takeIf { it.isNotBlank() }
            ?.lowercase()
        val fileName = if (extension != null) "${UUID.randomUUID()}.$extension" else UUID.randomUUID().toString()
        val key = "$dirName/$fileName"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(awsS3Properties.bucket)
            .key(key)
            .contentType(file.contentType ?: "application/octet-stream")
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.bytes))

        return key // 전체 URL 대신 key를 반환
    }

    /**
     * 파일 키를 사용하여 S3에서 파일을 삭제합니다.
     * @param fileKey 삭제할 파일의 키
     */
    fun deleteFile(fileKey: String) {
        if (fileKey.isBlank()) return
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(awsS3Properties.bucket)
            .key(fileKey)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }

    /**
     * 파일 키를 전체 S3 URL로 변환합니다.
     * @param key 변환할 파일의 키
     * @return 파일의 전체 URL
     */
    fun buildImageUrl(key: String): String {
        if (key.isBlank()) return ""

        if (awsS3Properties.endpoint.isNotBlank()) {
            return "${awsS3Properties.endpoint.trimEnd('/')}/${awsS3Properties.bucket}/$key"
        }

        return "https://${awsS3Properties.bucket}.s3.${awsS3Properties.region}.amazonaws.com/$key"
    }

    /**
     * 전체 S3 URL에서 파일 키를 추출합니다.
     * @param fileUrl 파일의 전체 URL
     * @return 추출된 파일 키
     */
    fun getKeyFromUrl(fileUrl: String): String {
        if (fileUrl.isBlank()) return ""
        // 엔드포인트 URL 또는 기본 S3 URL을 기반으로 key를 추출
        val baseUrl = if (awsS3Properties.endpoint.isNotBlank()) {
            "${awsS3Properties.endpoint.trimEnd('/')}/${awsS3Properties.bucket}/"
        } else {
            "https://${awsS3Properties.bucket}.s3.${awsS3Properties.region}.amazonaws.com/"
        }
        return fileUrl.substringAfter(baseUrl)
    }

    /**
     * 업로드할 파일의 유효성을 검사합니다.
     * - 파일이 비어있는지 확인
     * - 파일 타입이 이미지인지 확인 (MIME 타입 기준)
     * @param file 검사할 파일
     */
    private fun validateImage(file: MultipartFile) {
        if (file.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "image file is empty")
        }

        val contentType = file.contentType ?: ""
        if (!contentType.startsWith("image/")) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "only image files are allowed")
        }
    }
}
