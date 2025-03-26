//서버에 파일을 업로드
async function uploadToServer (formObj) {

    console.log("upload to server......")
    console.log(formObj)

    const response = await axios({
        method: 'post',
        url: '/upload',
        data: formObj,
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });

    return response.data
}

//서버에 특정 파일을 삭제
async function removeFileToServer(uuid, fileName)
{
    const response = await axios.delete(`/remove/${uuid}_${fileName}`)

    return response.data

}