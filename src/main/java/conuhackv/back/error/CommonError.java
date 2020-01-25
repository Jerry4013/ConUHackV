package conuhackv.back.error;

/**
 * @author: Jingchao Zhang
 * @createdate: 2019/05/04
 **/

public interface CommonError {

    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);
}
