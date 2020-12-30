package android.support.core.factory

import java.io.IOException
import java.io.InputStream
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.util.*
import javax.net.ssl.*


class TLSSocketFactory : SSLSocketFactory() {

    private var delegate: SSLSocketFactory
    private var sslContext: SSLContext

    init {
        val context = SSLContext.getInstance("TLS")
        context.init(null, arrayOf(systemDefaultTrustManager()), null)
        sslContext = context
        delegate = context.socketFactory
    }


    override fun getDefaultCipherSuites(): Array<String> {
        return delegate.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return delegate.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket? {
        return enableTLSOnSocket(delegate.createSocket())
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket? {
        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int): Socket? {
        return enableTLSOnSocket(delegate.createSocket(host, port))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket? {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort))
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket? {
        return enableTLSOnSocket(delegate.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket? {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort))
    }

    private fun enableTLSOnSocket(socket: Socket?): Socket? {
        if (socket != null && socket is SSLSocket) {
            socket.enabledProtocols = arrayOf("TLSv1.1", "TLSv1.2")
        }
        return socket
    }

    fun systemDefaultTrustManager(): X509TrustManager {
        try {
            val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
            )
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers = trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + Arrays.toString(
                    trustManagers
                )
            }
            return trustManagers[0] as X509TrustManager
        } catch (e: GeneralSecurityException) {
            throw AssertionError() // The system has no TLS. Just give up.
        }

    }

    fun sslFactory(inStream: InputStream): SSLSocketFactory {
        try {
            val cf = CertificateFactory.getInstance("X.509")
            val ca: Certificate
            ca = cf.generateCertificate(inStream)
            val kStore = KeyStore.getInstance(KeyStore.getDefaultType())
            kStore.load(null, null)
            kStore.setCertificateEntry("ca", ca)
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(kStore)
            sslContext.init(null, tmf.trustManagers, null)
            return sslContext.socketFactory
        } catch (e: GeneralSecurityException) {
            throw AssertionError() // The system has no TLS. Just give up.
        }
    }
}
