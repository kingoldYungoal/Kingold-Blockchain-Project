var pageNum = 1;
var pageSize = 10;
var classname;
var year;
var phone;
var M = {};
$(document).ready(function(){
    classname = $("#classSelect").val();
    year = $("#yearSelect").val();
    phone = $("#phone").val();
    initTable(year, classname);
});

//ajax获取后台数据
function initTable(year, classname) {
    var tbody="";
    $.ajax({
        type: 'get',
        dataType : "json",
        async: false,
        //url: "/student/studentlist",
        url: "../student/studentlist",
        data:{"teacherphone":phone,"year": year, "classname":classname, "pageNum":pageNum,"pageSize":pageSize},
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
                        var tbodys="";
                        $.ajax({
                            //url: "/student/studentlist",
                            url: "../student/studentlist",
                            type: "get",
                            dataType : "json",
                            data:{"teacherphone":phone,"year": year, "classname":classname, "pageNum":page,"pageSize":pageSize},
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
                // $("#pages ul li").hasClass('active')
                // {
                //     $("#pages ul li a").removeAttr('href');
                //     $("#pages ul li a").removeAttr('onclick');
                // }
            }else{
                $("#pages").html('');
            }
        }
    });
}

function BatchPrintPDF(){
    //alert("123");
}

function GoStudentInfo(obj) {
    var id = $(obj).attr("data-id");
    var roleid = $("#teacherid").val();
    window.location.href = "../student/studentinfo?id=" + id + "&roleid=" + roleid + "&role=2";
    //window.location.href = "/student/studentinfo?id=" + id;
}
