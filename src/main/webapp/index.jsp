<html>
<body>
<h2>Hello World!</h2>
8888CDN上传文件  CDN
<form name="form3" action="/file/uploadFileCDN.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="springmvc上传文件" />
</form>

8888CDN导入excel文件  CDN
<form name="form4" action="/manage/transport/upload_excel.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="导入" />
</form>
8888CDN导入excel文件  /manage/transport/consummate_transport.do
<form name="form5" action="/manage/transport/consummate_transport.do" method="post" enctype="multipart/form-data">
    <input type="text" name="id">
    <input type="file" name="salesList" />
    <input type="submit" value="导入配件入库" />
</form>
8888CDN导入excel文件  /manage/transport/host_transport.do
<form name="form5" action="/manage/transport/host_transport.do" method="post" enctype="multipart/form-data">
    <input type="text" name="id">
    <input type="file" name="salesList" />
    <input type="submit" value="导入主机入库" />
</form>
</body>
</html>
