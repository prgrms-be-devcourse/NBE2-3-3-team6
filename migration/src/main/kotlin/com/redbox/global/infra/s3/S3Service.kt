package com.redbox.global.infra.s3

import com.redbox.domain.community.attach.entity.Category
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody.fromInputStream
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.io.IOException
import java.net.URLEncoder.encode
import java.nio.charset.StandardCharsets
import java.time.Duration.ofMinutes

@Service
class S3Service(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
) {

    fun uploadFile(file: MultipartFile, category: Category, id: Long, fileName: String) {
        try {
            val request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(getKey(category, id, fileName))
                .contentType(file.contentType)
                .build()

            s3Client.putObject(
                request,
                fromInputStream(file.inputStream, file.size)
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun deleteFile(category: Category, id: Long, fileName: String) {
        val request = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(getKey(category, id, fileName))
            .build()

        s3Client.deleteObject(request)

        // 빈 디렉토리 확인 후 삭제
        deleteDirectory(category, id)
    }

    fun deleteDirectory(category: Category, id: Long) {
        try {
            // 해당 디렉토리의 prefix로 모든 객체 나열
            val prefix = "${category.text}/$id/"

            val listRequest = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build()

            val listResponse: ListObjectsV2Response = s3Client.listObjectsV2(listRequest) ?: ListObjectsV2Response.builder().build()

            // 객체가 없다면 디렉토리도 삭제
            if (listResponse.contents().isEmpty()) {
                val deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(prefix)
                    .build()

                s3Client.deleteObject(deleteRequest)
            }
        } catch (e: Exception) {
            throw S3Exception("Error while deleting directory", e)
        }
    }

    fun generatePresignedUrl(category: Category, id: Long, fileName: String, originalFilename: String): String {
        try {
            val encodedFilename = encode(originalFilename, StandardCharsets.UTF_8)

            val presignRequest: GetObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(ofMinutes(10))
                .getObjectRequest{ b: GetObjectRequest.Builder ->
                    b.bucket(bucket)
                        .key(getKey(category, id, fileName))
                        .responseContentDisposition("attachment; filename=\"$encodedFilename\"")
                }
                .build()

            return s3Presigner.presignGetObject(presignRequest)
                ?.url()
                .toString()
        } catch (e: Exception) {
            throw S3Exception("Failed to generate presigned URL", e)
        }
    }

    companion object {
        private fun getKey(category: Category, id: Long, fileName: String): String {
            return "${category.text}/$id/$fileName"
        }
    }
}