<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
Hello ${name}!
<#--遍历数据模型的list学生信息 数据模型中的名称为stus-->
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>金额</td>
        <td>出生日期</td>
    </tr>
    <#if stus??>
    <#list stus as stu>
        <tr>
            <td>${stu_index}</td>
            <td <#if stu.name == '小明' >style="background:red;"</#if>${stu.name}</td>
            <td>${(stu.age)! ''}</td>
            <td <#if stu.mondy gt 300>style="background:red;"</#if>>${stu.mondy}</td>
        </tr>
    </#list>
    </#if>
</table>
<br/>
list的大小${stus?size}
<br/>
遍历数据模型中的stuMap(Map数据)
姓名：${stuMap['stu1'].name}
姓名：${stuMap.stu1.age}
<br/>
遍历map中的key stuMap?keys 就是key列表
<#list stuMap?keys as key>
    ${key}<br/>
    ${stuMap[key].name}
</#list>

</body>
</html>