+ 使用密钥对而不是单一字符串作为 Token :<https://www.zhihu.com/question/54513153>
+ Api：签名验证机制:<https://github.com/Eliacy/YYMiOS/wiki/Api%EF%BC%9A%E7%AD%BE%E5%90%8D%E9%AA%8C%E8%AF%81%E6%9C%BA%E5%88%B6>
为了实现基本的防抓取机制，对绝大多数采用了 Api 签名验证，在保证签名秘钥不泄露的前提下，具有一定的数据抓取防御能力。


+ 无论是对称加密还是非对称加密，防止请求伪造都是建立在秘钥和签名算法不泄露的前提下，实际上通过反编译客户端代码就能破解。
  防止请求篡改可以使用非对称加密算法，如rsa
  
  
