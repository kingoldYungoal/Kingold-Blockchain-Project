package com.kingold.educationblockchain.util;

/**
 * @Description: 将结果转换为封装后的对象
 */
public class ResultResponse {
    private final static String SUCCESS = "success";

    private final static String Fail = "fail";

    public static <T> RetResult<T> makeOKRsp() {
        return new RetResult<T>().setCode(ResultCode.SUCCESS).setMsg(SUCCESS);
    }

    public static <T> RetResult<T> makeOKRsp(T data) {
        return new RetResult<T>().setCode(ResultCode.SUCCESS).setMsg(SUCCESS).setData(data);
    }

    public static <T> RetResult<T> makeErrRsp(T message) {
        return new RetResult<T>().setCode(ResultCode.FAIL).setMsg(Fail).setData(message);
    }

    public static <T> RetResult<T> makeRsp(int code, String msg) {
        return new RetResult<T>().setCode(code).setMsg(msg);
    }

    public static <T> RetResult<T> makeRsp(int code, String msg, T data) {
        return new RetResult<T>().setCode(code).setMsg(msg).setData(data);
    }
}