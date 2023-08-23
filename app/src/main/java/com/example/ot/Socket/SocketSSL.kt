import io.socket.client.IO
import okhttp3.OkHttpClient
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object SocketSSL {
    val okHttpClient: OkHttpClient?
        get() {
            try {
                val sc = SSLContext.getInstance("SSL")
                sc.init(null, arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }
                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                }), SecureRandom())
                val builder = OkHttpClient.Builder()
                builder.hostnameVerifier({ hostname, session -> true })
                builder.sslSocketFactory(sc.socketFactory, object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }
                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                })
                return builder.build()
            } catch (ex: NoSuchAlgorithmException) {
                ex.printStackTrace()
            } catch (ex: KeyManagementException) {
                ex.printStackTrace()
            }
            return null
        }
    fun set(options: IO.Options) {
        val okHttpClient = okHttpClient
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient)
        IO.setDefaultOkHttpCallFactory(okHttpClient)
        options.callFactory = okHttpClient
        options.webSocketFactory = okHttpClient
    }
}