package tmoney.gbi.bms.config;

import com.github.tocrhz.mqtt.properties.MqttConfigAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import com.github.tocrhz.mqtt.properties.MqttClientRegistry;
import com.github.tocrhz.mqtt.properties.MqttConfigAdapter;
import com.github.tocrhz.mqtt.properties.MqttConnectionProperties;
import org.springframework.stereotype.Component;
import java.util.UUID;

//@Component
@Slf4j
public class MqttConfig extends MqttConfigAdapter {

//    @Override
//    public void beforeConnect(String clientId, MqttConnectOptions options) {
//        try {
//            options.setSocketFactory(createSocketFactory(
//                    "path/to/your/ca.pem",    // CA 인증서 경로
//                    "path/to/your/client.crt", // 클라이언트 인증서 경로
//                    "path/to/your/client.key", // 클라이언트 개인 키 경로
//                    "your_password"            // 키 저장소/개인 키 비밀번호
//            ));
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create SSLSocketFactory", e);
//        }
//    }
//
//    private SSLSocketFactory createSocketFactory(String caCrtFile, String crtFile, String keyFile, String password) throws Exception {
//        java.security.Security.addProvider(new BouncyCastleProvider());
//
//        // CA 인증서 로드 (TrustStore)
//        PEMParser caParser = new PEMParser(new FileReader(caCrtFile));
//        X509CertificateHolder caCertHolder = (X509CertificateHolder) caParser.readObject();
//        caParser.close();
//        X509Certificate caCert = new org.bouncycastle.cert.jcajce.JcaX509CertificateConverter().getCertificate(caCertHolder);
//
//        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        trustStore.load(null, null);
//        trustStore.setCertificateEntry("ca-certificate", caCert);
//
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        tmf.init(trustStore);
//
//        // 클라이언트 인증서와 개인 키 로드 (KeyStore)
//        PEMParser certParser = new PEMParser(new FileReader(crtFile));
//        X509CertificateHolder certHolder = (X509CertificateHolder) certParser.readObject();
//        certParser.close();
//        X509Certificate clientCert = new org.bouncycastle.cert.jcajce.JcaX509CertificateConverter().getCertificate(certHolder);
//
//        PEMParser keyParser = new PEMParser(new FileReader(keyFile));
//        Object keyObject = keyParser.readObject();
//        keyParser.close();
//
//        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
//        KeyPair keyPair = converter.getKeyPair((PEMKeyPair) keyObject);
//
//        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        keyStore.load(null, null);
//        keyStore.setCertificateEntry("certificate", clientCert);
//        keyStore.setKeyEntry("private-key", keyPair.getPrivate(), password.toCharArray(), new java.security.cert.Certificate[]{clientCert});
//
//        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        kmf.init(keyStore, password.toCharArray());
//
//        // SSLContext 생성 및 초기화
//        SSLContext context = SSLContext.getInstance("TLSv1.2");
//        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
//
//        return context.getSocketFactory();
//    }
}
