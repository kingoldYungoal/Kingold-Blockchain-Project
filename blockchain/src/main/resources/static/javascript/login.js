$(function () {
    var M = {};
    var wait=60;
    var re = /^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/;
    var $codebtn = $("#codebtn");

    $("#authcode").val("");
    $("input:radio[name='role']").get(0).checked = true ;

    $("#codebtn").click(function () {
        var disabled = $("#codebtn").attr("disabled");
        if(disabled){
            return false;
        }
        time();
        var phonenumber = $("#phonenumber").val();
        if ($.trim(phonenumber) == "") {
            //结束倒计时
            $codebtn.attr('disabled',false);
            $codebtn.val("获取验证码");
            wait = 0;
            M.dialog13 = jqueryAlert({
                'icon': '../images/alertimgs/warning.png',
                'content': '请输入手机号码',
                'closeTime': 2000,
            })
            M.dialog13.show();
            $("#phonenumber").focus();
            return false;
        }
        if (!phonenumber.match(re)) {
            //结束倒计时
            $codebtn.attr('disabled',false);
            $codebtn.val("获取验证码");
            wait = 0;
            M.dialog13 = jqueryAlert({
                'icon': '../images/alertimgs/warning.png',
                'content': '请输入正确的手机号码',
                'closeTime': 2000,
            })
            M.dialog13.show();
            $("#phonenumber").val("");
            $("#phonenumber").focus();
            return false;
        }

        var datas = {
            'phone' : phonenumber
        };

        //获取手机号码是否在表中存在
        $.ajax({
            type:"post",
            url:"../login/IsExistPhone",
            data: JSON.stringify(datas),
            contentType : 'application/json',
            dataType : 'json',
            //async: false,
            success:function (data) {
                if (data) {
                    //发送验证码
                    $.ajax({
                        type:"post",
                        url:"../smscode/sendsmscode",
                        data: JSON.stringify(datas),
                        contentType : 'application/json',
                        dataType : 'json',
                        //async: false,
                        success:function (data) {
                            if (data.success){
                                if (data.result.result == 0){
                                    M.dialog13 = jqueryAlert({
                                        'icon': '../images/alertimgs/right.png',
                                        'content': '验证码发送成功',
                                        'closeTime': 2000,
                                    })
                                    M.dialog13.show();
                                }
                                if (data.result.result == 32){
                                    //结束倒计时
                                    $codebtn.attr('disabled',false);
                                    $codebtn.val("获取验证码");
                                    wait = 0;
                                    M.dialog13 = jqueryAlert({
                                        'icon': '../images/alertimgs/warning.png',
                                        'content': '当天验证码发送次数已达最大',
                                        'closeTime': 2000,
                                    })
                                    M.dialog13.show();
                                }
                                if (data.result.result == 33){
                                    // 结束倒计时
                                    $codebtn.attr('disabled',false);
                                    $codebtn.val("获取验证码");
                                    wait = 0;
                                    M.dialog13 = jqueryAlert({
                                        'icon': '../images/alertimgs/warning.png',
                                        'content': '验证码已发送',
                                        'closeTime': 2000,
                                    })
                                    M.dialog13.show();
                                }
                            }
                        },error:function (data) {
                            // 结束倒计时
                            $codebtn.attr('disabled',false);
                            $codebtn.val("获取验证码");
                            wait = 0;
                            M.dialog13 = jqueryAlert({
                                'icon': '../images/alertimgs/warning.png',
                                'content': '验证码发送失败',
                                'closeTime': 2000,
                            })
                            M.dialog13.show();
                        }
                    });
                }else{
                    //结束倒计时
                    $codebtn.attr('disabled',false);
                    $codebtn.val("获取验证码");
                    wait = 0;
                    M.dialog13 = jqueryAlert({
                        'icon': '../images/alertimgs/warning.png',
                        'content': '此号码不存在',
                        'closeTime': 2000,
                    })
                    M.dialog13.show();
                }
            },error:function (data) {
                //结束倒计时
                $codebtn.attr('disabled',false);
                $codebtn.val("获取验证码");
                wait = 0;
                M.dialog13 = jqueryAlert({
                    'icon': '../images/alertimgs/warning.png',
                    'content': '验证码发送失败',
                    'closeTime': 2000,
                })
                M.dialog13.show();
            }
        });
    });

     $("#loginbtn").click(function () {
         var phonenumber = $("#phonenumber").val();
         var authcode = $("#authcode").val();
         var role = $("input[name='role']:checked").val();
         if ($.trim(phonenumber) == "") {
             M.dialog13 = jqueryAlert({
                 'icon': '../images/alertimgs/warning.png',
                 'content': '请输入手机号码',
                 'closeTime': 2000,
             })
             M.dialog13.show();

             $("#phonenumber").focus();
             return false;
         }
         if (!re.test(phonenumber)) {
             M.dialog13 = jqueryAlert({
                 'icon': '../images/alertimgs/warning.png',
                 'content': '请输入正确的手机号码',
                 'closeTime': 2000,
             })
             M.dialog13.show();
             $("#phonenumber").val("");
             $("#phonenumber").focus();
             return false;
         }
         if ($.trim(authcode) == "") {
             M.dialog13 = jqueryAlert({
                 'icon': '../images/alertimgs/warning.png',
                 'content': '请输入验证码',
                 'closeTime': 2000,
             })
             M.dialog13.show();
             $("#authcode").focus();
             return false;
         }
         var datas = {
             'phone' : phonenumber,
             'role': role
         };
         $("#loginform").submit();
         // 手机号码是否存在

         // $.ajax({
         //     type:"post",
         //     url:"../login/IsExistPhoneByRole",
         //     data: JSON.stringify(datas),
         //     contentType : 'application/json',
         //     dataType : 'json',
         //     success:function (data) {
         //         if (data){
         //             // 验证码是否正确判断
         //             // 获取验证码
         //             $.ajax({
         //                 type:"post",
         //                 url:"../smscode/getsmscode",
         //                 data: JSON.stringify(datas),
         //                 contentType : 'application/json',
         //                 dataType : 'json',
         //                 success:function (data) {
         //                     if (data.success){
         //                         var verifycodes = data.result.result;
         //                         if (verifycodes == authcode){
         //                             M.dialog13 = jqueryAlert({
         //                                 'icon': '../images/alertimgs/right.png',
         //                                 'content': '验证码验证成功',
         //                                 'closeTime': 2000,
         //                             })
         //                             M.dialog13.show();
         //
         //                             $("#loginform").submit();
         //                         }else{
         //                             M.dialog13 = jqueryAlert({
         //                                 'icon': '../images/alertimgs/warning.png',
         //                                 'content': '验证码验证失败',
         //                                 'closeTime': 2000,
         //                             })
         //                             M.dialog13.show();
         //                         }
         //                     }
         //                 },error:function (data) {
         //                     M.dialog13 = jqueryAlert({
         //                         'icon': '../images/alertimgs/warning.png',
         //                         'content': '验证码验证失败',
         //                         'closeTime': 2000,
         //                     })
         //                     M.dialog13.show();
         //                 }
         //             });
         //         }else{
         //             M.dialog13 = jqueryAlert({
         //                 'icon': '../images/alertimgs/warning.png',
         //                 'content': '此号码不存在',
         //                 'closeTime': 2000,
         //             })
         //             M.dialog13.show();
         //         }
         //     },error:function (data) {
         //         M.dialog13 = jqueryAlert({
         //             'icon': '../images/alertimgs/warning.png',
         //             'content': '验证码验证失败',
         //             'closeTime': 2000,
         //         })
         //         M.dialog13.show();
         //     }
         // });
     });

    function time() {
        if (wait == 0) {
            $codebtn.attr('disabled',false);
            $codebtn.val("获取验证码");
            wait = 60;
            return;
        } else {
            $codebtn.attr("disabled", true);
            $codebtn.val("重新发送(" + wait + ")");
            wait--;

            setTimeout(function () {
                    time()
                },
                1000)
        }
    }
});