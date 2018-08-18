package http;

/**
 * 对外提供加密解密<br/>
 */
public interface EncryptFuncs {
    public String encrypt(String plainText);

    public String decrypt(String cipherText);

    public boolean accept(String mHttpReqTag); // 允许加密解密吗,万一某个不要加密解密怎么办
}
