var pageNum = 1;
var pageSize = 10;
var classname;
var year;
var teacherid;
var phone;
var M = {};
// 判断浏览器类型
var userAgent = navigator.userAgent;

$(document).ready(function(){
    phone = $("#phone").val();
    var printA ="";
    if (userAgent.indexOf("Chrome") > -1 && userAgent.indexOf("Edge") <= -1) {
        printA += "<a class='print-preview' id='printbtn' onclick='BatchPrintPDF()'>证书批量打印</a>";
    }else{
        printA += "<a class='print-preview' id='printbtn'>证书批量打印</a>";
    }
    $("#printdiv").html(printA);
    if (userAgent.indexOf("Chrome") <= -1 || userAgent.indexOf("Edge") > -1){
        $('a.print-preview').printPreview();
    }
    initTable();
});


//ajax获取后台数据
function initTable() {
    classname = $("#classSelect").val();
    year = $("#yearSelect").val();
    var tbody="";
    $.ajax({
        type: 'get',
        dataType : "json",
        async: false,
        url: "../student/studentlist",
        data:{"teacherphone":phone, "classname":classname, "year": year, "pageNum":pageNum,"pageSize":pageSize},
        error: function () {//请求失败处理函数
            M.dialog13 = jqueryAlert({
                'icon': '../images/alertimgs/warning.png',
                'content': '请求失败',
                'closeTime': 2000,
            })
            M.dialog13.show();
        },
        success:function(data){ //请求成功后处理函数。
            if (data.items.length > 0){
                for (var i = 0;i <data.items.length;i++){
                    var trs = "";
                    trs += "<tr data-id='"+ data.items[i].kg_studentprofileid +"' onclick='GoStudentInfo(this)'><td></td><td style='padding-left:20px;text-align: left;'>" + data.items[i].kg_fullname + "</td><td>"+data.items[i].kg_educationnumber + "</td><td>"+data.items[i].kg_jointime+"</td><td>"+data.items[i].kg_sex+"</td><td>"+data.items[i].kg_parentName+"</td><td style='padding-right:20px;text-align: right;'>"+data.items[i].kg_parentPhoneNumber+"</td><td></td></tr>";
                    tbody+=trs;
                }
            }

            $("#datadiv table tbody").html(tbody);

            if (data.totalPage > 1){
                var pageCount = data.totalPage; //取到pageCount的值
                var currentPage = data.currentPage; //得到currentPage
                var options = {
                    bootstrapMajorVersion: 2, //版本
                    currentPage: currentPage, //当前页数
                    totalPages: pageCount, //总页数
                    numberOfPages: pageCount,
                    shouldShowPage:true,
                    itemTexts: function (type, page, currentPage) {
                        switch (type) {
                            case "first":
                                return "首页";
                            case "prev":
                                return "上一页";
                            case "next":
                                return "下一页";
                            case "last":
                                return "末页";
                            case "page":
                                return page;
                        }
                    },//点击事件，用于通过Ajax来刷新整个list列表
                    onPageClicked: function (event, originalEvent, type, page) {
                        $("#datadiv table tbody").html("");
                        var tbodys = "";
                        $.ajax({
                            url: "../student/studentlist",
                            type: "get",
                            dataType : "json",
                            data:{"teacherphone":phone,"classname":classname,"year": year, "pageNum":page,"pageSize":pageSize},
                            success: function (data) {
                                if (data.items.length > 0){
                                    for (var i = 0;i <data.items.length;i++){
                                        var trs = "";
                                        trs += "<tr data-id='"+ data.items[i].kg_studentprofileid +"' onclick='GoStudentInfo(this)'><td></td><td style='padding-left:20px;text-align: left;'>" + data.items[i].kg_fullname + "</td><td>"+data.items[i].kg_educationnumber + "</td><td>"+data.items[i].kg_jointime+"</td><td>"+data.items[i].kg_sex+"</td><td>"+data.items[i].kg_parentName+"</td><td style='padding-right:20px;text-align: right;'>"+data.items[i].kg_parentPhoneNumber+"</td><td></td></tr>";
                                        tbodys+=trs;
                                    }
                                }
                                $("#datadiv table tbody").html(tbodys);
                            }
                        });
                    }
                };
                $('#pages').bootstrapPaginator(options);
            }else{
                $("#pages").html('');
            }
        }
    });
}

function BatchPrintPDF(){
    classname = $("#classSelect").val();
    year = $("#yearSelect").val();
    teacherid = $("#teacherid").val();
    var datas = {
        'className' : classname,
        'year': year,
        'teacherId': teacherid
    };
    $("#certIframe").html("");
    var iframes = "";

    $.ajax({
        type:"post",
        url:"../electronicscertificate/certificatelist",
        data: JSON.stringify(datas),
        contentType : 'application/json',
        dataType : 'json',
        async: false,
        success:function (data) {
            if (data != null && data.length > 0){
                iframes += "<iframe frameborder='0' style='width: 0px;height: 0px;page-break-after:always;' id='printIframe' src='../electronicscertificate/certificate/showPdf'>";
                iframes += "</iframe>";
                $("#certIframe").html(iframes);
                $("#printIframe")[0].contentWindow.print();
            }else{
                M.dialog13 = jqueryAlert({
                    'icon': '../images/alertimgs/warning.png',
                    'content': '暂无可以打印的证书信息',
                    'closeTime': 2000,
                })
                M.dialog13.show();
            }
        },error: function () {//请求失败处理函数
            M.dialog13 = jqueryAlert({
                'icon': '../images/alertimgs/warning.png',
                'content': '请求失败',
                'closeTime': 2000,
            })
            M.dialog13.show();
        }
    });
}

function GoStudentInfo(obj) {
    var id = $(obj).attr("data-id");
    var roleid = $("#teacherid").val();
    window.location.href = "../student/studentinfo?id=" + id + "&roleid=" + roleid + "&role=2";
}

function loading() {
    $('body').loading({
        loadingWidth:240,
        title:'请稍等',
        name:'test',
        discription:'正在全力加载证书打印预览',
        direction:'column',
        type:'origin',
        originBg:'#000',
        originDivWidth:40,
        originDivHeight:40,
        originWidth:6,
        originHeight:6,
        smallLoading:false,
        loadingBg:'#004a80',
        loadingMaskBg:'rgba(123,122,222,0.2)'
    });
}
