package dontstopjo.ootdrop.domain.post.service

import org.jsoup.Jsoup
import org.springframework.stereotype.Service

@Service
class ImageLinkService {

    fun extractClothingImage(url: String): String? {
        return try {
            // 1. 브라우저인 것처럼 헤더를 설정하여 차단 방지 + 타임아웃 설정
            val document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .timeout(5000)
                .get()

            // 2. [1순위] 메타 데이터 확인 (대부분의 사이트에서 가장 정확한 대표 이미지)
            val ogImage = document.select("meta[property=og:image]").attr("abs:content")
            if (ogImage.isNotBlank()) return ogImage

            val twitterImage = document.select("meta[name=twitter:image]").attr("abs:content")
            if (twitterImage.isNotBlank()) return twitterImage

            // 3. [2순위] 본문 이미지 태그 분석
            val imgElements = document.select("img")

            for (element in imgElements) {
                // src뿐만 아니라 Lazy Loading용 속성들도 함께 체크
                val imageUrl = (element.attr("abs:data-src").takeIf { it.isNotBlank() }
                    ?: element.attr("abs:data-original").takeIf { it.isNotBlank() }
                    ?: element.attr("abs:src")).lowercase()

                if (imageUrl.isBlank()) continue

                // 4. 의류 사진이 아닐 확률이 높은 노이즈 필터링
                val isNoise = imageUrl.contains("logo") ||
                        imageUrl.contains("icon") ||
                        imageUrl.contains("banner") ||
                        imageUrl.contains("button") ||
                        imageUrl.contains("profile") ||
                        imageUrl.contains("ad_") ||
                        imageUrl.contains("loading") ||
                        imageUrl.endsWith(".gif") || // 움짤 제외
                        imageUrl.endsWith(".svg")    // 벡터 아이콘 제외
/*
                // 5. 간단한 크기 유추 (alt 값에 옷 관련 키워드가 있으면 가산점)
                val altText = element.attr("alt").lowercase()
                val isClothingContext = altText.contains("자켓") || altText.contains("팬츠") ||
                        altText.contains("티셔츠") || altText.contains("옷") ||
                        altText.contains("coat") || altText.contains("pants")
*/
                if (!isNoise) {
                    // 노이즈가 아니면서 옷 키워드가 있거나,
                    // 키워드가 없더라도 최소한의 필터링을 통과한 첫 번째 이미지를 반환
                    return element.attr("abs:src")
                }
            }
            null
        } catch (e: Exception) {
            // 로그를 남기거나 null 반환
            null
        }
    }
}