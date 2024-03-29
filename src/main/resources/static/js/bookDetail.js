var dataForChild
    ,hello
    ,actionType;
layui.use(['layer','element','table','form','laydate'], function(){
    var $ = layui.jquery;
    var element = layui.element//导航的hover效果、二级菜单等功能，需要依赖element模块
        ,layer = layui.layer
        ,table = layui.table
        ,form = layui.form
        ,laydate=layui.laydate;
    //填写select
    $.ajax({
        url: basePath + "tushuxinxiguanli/listAllBookMsgIdAndName"
        , type: 'get'
        , success: function (res) {
            // console.log(res.data);
            MOD.Form.fillSelect($("#bookName"), res.data, "bookMessageId", "bookMassageName");
            if(parent.actionType=='detail'||parent.actionType=='edit'||parent.actionType == 'borrow'||parent.actionType == 'return'){
                if (parent.dataForChild != null)
                MOD.Form.fillForm($('#bookDetail'),parent.dataForChild);
                form.render();
            }
            form.render()
            console.log($("#bookName").val())
            $.ajax({
                url:basePath+'tushuxinxiguanli/getAuthorAndPublisherByBookMsgId'
                ,data:{
                    bookMessageId:$("#bookName").val()
                }
                ,success:function (res) {
                    console.log(res);
                    $("input[name='author']").val(res.data.author);
                    $("input[name='publisher']").val(res.data.publisher);
                    form.render();
                }
            });
        }
    });
    $.ajax({
        url: basePath + "data/listAllBookStatus"
        , type: 'get'
        , success: function (res) {
            // console.log(res.data);
            MOD.Form.fillSelect($("#bookStatusId"), res.data, "bookStatusId", "bookStatusName");
            if(parent.actionType=='detail'||parent.actionType=='edit'||parent.actionType == 'borrow'||parent.actionType == 'return'){
                if (parent.dataForChild != null)
                MOD.Form.fillForm($('#bookDetail'),parent.dataForChild);
                form.render();
            }
            form.render()
        }
    });
    $.ajax({
        url: basePath + "data/listAllQuality"
        , type: 'get'
        , success: function (res) {
            // console.log(res.data);
            MOD.Form.fillSelect($("#qualityId"), res.data, "qualityId", "qualityName");
            if(parent.actionType=='detail'||parent.actionType=='edit'||parent.actionType == 'borrow'||parent.actionType == 'return'){
                if (parent.dataForChild != null)
                MOD.Form.fillForm($('#bookDetail'),parent.dataForChild);
                form.render();
            }
            form.render()
        }
    });
    if (parent.dataForChild != null)
    $.ajax({
        url: basePath + 'churukuguanli/listBookHistory'
        , type: 'get'
        ,data:{
            bookId: parent.dataForChild.bookId
        }
        , success: function (res) {
            if (parent.actionType == 'detail' || parent.actionType == 'edit' || parent.actionType == 'borrow'||parent.actionType == 'return') {
                $("#reviewLog").val(res.data);
                form.render();
            }
        }
    });
    console.log(parent.actionType);
    if(parent.actionType=='detail'){
        $("form input").attr("readonly","readonly");
        $("form textarea").attr("readonly","readonly");
        $("form select").attr("readonly", "readonly").attr("disabled", "disabled");
        $("button").addClass("layui-hide");
    }else if(parent.actionType=='add'){
        $("input[name='publisher']").attr("readonly", "readonly");
        $("input[name='author']").attr("readonly", "readonly");
        $("#IDinput").addClass("layui-hide");
        $("#bookNum").removeClass("layui-hide")
    }else if(parent.actionType=='edit'){
        // console.log(parent.dataForChild);
        $("#bookName").attr("readonly", "readonly").attr("disabled", "disabled");
        $("input[name='publisher']").attr("readonly", "readonly");
        $("input[name='bookId']").attr("readonly", "readonly");
        $("input[name='author']").attr("readonly", "readonly");
    }else if (parent.actionType == 'borrow') {
        $("form input").attr("readonly","readonly");
        $("form textarea").attr("readonly","readonly");
        $("form select").attr("readonly", "readonly").attr("disabled", "disabled");
        $("#save").addClass("layui-hide");
        $("#borrow").removeClass("layui-hide");
    }else if(parent.actionType == 'return'){
        $("#save").addClass("layui-hide");
        $("input").attr("readonly", "readonly");
        $("form textarea").attr("readonly","readonly");
        $("form select").attr("readonly", "readonly").attr("disabled", "disabled");
        $("form select[name='qualityId']").attr("readonly", false).attr("disabled", false);
        $("#return").removeClass("layui-hide");
    } else if (parent.actionType == 'fastBorrow') {
        $("#save").addClass("layui-hide");
        $("#borrow").removeClass("layui-hide");
        $("input").attr("readonly", "readonly");
        $("form textarea").attr("readonly","readonly");
        $("form select").attr("readonly", "readonly").attr("disabled", "disabled");
        $("form select[name='qualityId']").attr("readonly", false).attr("disabled", false);
        $("input[name='bookId']").prop("readonly",false)
        $("input[name='bookId']").blur(function (data) {
            $.ajax({
                url:basePath+'churukuguanli/getBookInfoById'
                ,type:'get'
                ,data:{
                    bookId: $("input[name='bookId']").val()
                }
                ,success:function (res) {
                    if (res.code == 0) {
                        $.ajax({
                            url: basePath + 'churukuguanli/listBookHistory'
                            , type: 'get'
                            ,data:{
                                bookId: $("input[name='bookId']").val()
                            }
                            , success: function (res) {
                                $("#reviewLog").val(res.data);
                                form.render();
                            }
                        });
                        MOD.Form.fillForm($('#bookDetail'),res.data);
                        if (res.data.bookStatusId == 1) {
                            $("#return").removeClass("layui-hide");
                            $("#borrow").addClass("layui-hide");
                        }
                        form.render();
                    }else {
                        layer.alert(res.msg);
                    }
                }
            })
        });
    }

    form.on('select(bookName)', function(d){
        $.ajax({
            url:basePath+'tushuxinxiguanli/getAuthorAndPublisherByBookMsgId'
            ,data:{
                bookMessageId:d.value
            }
            ,success:function (res) {
                $("input[name='author']").val(res.data.author);
                $("input[name='publisher']").val(res.data.publisher);
            }
        })
    });
    form.on('submit(save)',function(data){//保存
        var myurl;
        if(parent.actionType=='edit')
        {
           //修改
            myurl = "churukuguanli/updateBook";
        }
        else if(parent.actionType=='add')
        {
            //添加
            myurl = "churukuguanli/addBook";
        }
        $.ajax({
            url:basePath+myurl
            , type: 'post'
            , data: data.field
            , success: function (res) {
                if (res.code === 0) {
                    layer.alert("操作成功！", function () {
                        parent.layui.table.reload('table2');
                        var myWindow = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(myWindow); //再执行关闭
                    });
                }
                else layer.alert(""+res.msg, {icon: 5});
            },
            error:function(jqXHR)
            {
                // console.log('请求错误,错误的原因为:'+jqXHR.status)
            }
        });
        return false;
    });

    $("#borrow").click(function (data) {
        // layer.prompt({title: '请询问订单号，填写并确认', formType: 0}, function (pass, index1) {
        //     if (pass == data)
        //         layer.close(index1);
        //     layer.prompt({title: '请输入用户名，并确认', formType: 0}, function (userId, index2) {
        //         layer.closeAll();
        //         $.ajax({
        //             url: basePath + 'borrow/borrowBook'
        //             , data: {
        //                   email: userId
        //                 , code: pass
        //                 , bookId: $("input[name='bookId']").val()
        //             }
        //             , success: function (res) {
        //                 layer.alert(res.msg);
        //             }
        //             , error: function (res) {
        //                 layer.alert(res.msg);
        //             }
        //         });
        //     });
        // });
        var bookId = $("input[name='bookId']").val();
        if (bookId == "" || bookId == null) {
            layer.alert("请先输入ID",{icon: 5})
        }else {
            dataForChild = {
                bookId: bookId
            };
            layer.open({
                type: 2,
                title:"借出图书",
                area: ['400px', '350px'],
                skin: 'layui-layer-rim layui-layer-molv', //加上边框
                content:basePath+'borrow'
            });
        }
    });
    $("#return").click(function (data) {
        $.ajax({
            url:basePath+'borrow/backBook'
            ,data:{
                bookId: $("input[name='bookId']").val()
                ,qualityId:$("input[name='qualityId']").val()
            }
            , success: function (res) {
                if (res.code === 0) {
                    layer.alert("操作成功！", function () {
                        var myWindow = parent.layer.getFrameIndex(window.name);
                        parent.layer.close(myWindow); //再执行关闭
                    });
                }
                else layer.alert(""+res.msg, {icon: 5});
            },
            error:function(jqXHR)
            {
                // console.log('请求错误,错误的原因为:'+jqXHR.status)
            }
        })
    })
    //关闭按钮
    $('button[type=close]').click(function(){
        var mywindow = parent.layer.getFrameIndex(window.name);
        parent.layer.close(mywindow); //再执行关闭
    });

    form.verify({
        moreThanOne:function (value) {
            if (value < 0||value>1000) {
                return "数值不合理";
            }
        }
    });

});