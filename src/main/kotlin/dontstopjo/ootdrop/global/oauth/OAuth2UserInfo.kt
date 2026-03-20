package dontstopjo.ootdrop.global.oauth

/**
 * OAuth2 제공자로부터 받은 사용자 정보를 추상화한 인터페이스
 * 각 OAuth2 제공자(Google, Kakao 등)마다 다른 응답 형식을 통일된 방식으로 처리하기 위한 인터페이스
 */
interface OAuth2UserInfo {
    /**
     * OAuth2 제공자가 부여한 고유 사용자 식별자
     */
    fun getProviderId(): String

    /**
     * OAuth2 제공자 이름 (google, kakao 등)
     */
    fun getProvider(): String

    /**
     * 사용자 이메일 주소
     */
    fun getEmail(): String

    /**
     * 사용자 이름
     */
    fun getName(): String

    /**
     * 사용자 프로필 이미지 URL (선택적)
     */
    fun getProfileImage(): String
}

/**
 * Kakao OAuth2 제공자의 사용자 정보 구현체
 * Kakao OAuth2 API로부터 받은 attributes를 OAuth2UserInfo 형식으로 변환
 *
 * @property attributes Kakao OAuth2 제공자로부터 받은 사용자 속성 맵
 */
class KakaoOAuth2UserInfo(
    private val attributes: Map<String, Any>
) : OAuth2UserInfo {

    /**
     * Kakao의 고유 사용자 식별자 (id 필드)
     */
    override fun getProviderId(): String = attributes["id"].toString()

    /**
     * 제공자 이름 반환 (kakao)
     */
    override fun getProvider(): String = "kakao"

    /**
     * Kakao는 사용자 정보를 kakao_account 내부에 포함함
     */
    private val kakaoAccount = attributes["kakao_account"] as? Map<String, Any>
    private val profile = kakaoAccount?.get("profile") as? Map<String, Any>

    /**
     * 사용자 이메일 주소
     */
    override fun getEmail(): String = kakaoAccount?.get("email") as? String ?: ""

    /**
     * 사용자 이름
     */
    override fun getName(): String = profile?.get("nickname") as? String ?: ""

    /**
     * 프로필 이미지 URL
     */
    override fun getProfileImage(): String = profile?.get("profile_image_url") as? String ?: ""
}
