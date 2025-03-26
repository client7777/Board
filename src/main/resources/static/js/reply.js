/*
axios를 사용해서 댓글관련 REST API 요청을 수행하는 비동기 함수들
*/
async function get1(bno)
{
    //특정 게시물의 댓글 목록을 가져오는 함수
    const result = await axios.get(`/replies/list/${bno}`) // 해당 게시물의 댓글 리스트를 가져옴

    return result;

}

async function getList({bno, page, size, goLast})
{
    // 특정 게시물의 댓글 목록을 가져오되 페이지네이션을 고려함
    const result = await axios.get(`/replies/list/${bno}`, {params: {page, size}})

    if(goLast)
    {
        const total = result.data.total
        const lastPage = parseInt(Math.ceil(total/size))
        // 전체 댓글 개수를 가져와 마지막 페이지 번호를 계산

        return getList({bno:bno, page:lastPage, size:size})
        //마지막 페이지의 댓글 목록을 다시 요청, getList() 재귀 호출
    }
    return result.data
}

//새로운 댓글 추가
async function addReply(replyObj)
{
    const response = await axios.post(`/replies/`, replyObj) // 댓글 정보를 replyObj로 전달ㄴ

    return response.data // 응답의 data 부분을 반환
}

//특정 댓글 조회
async function getReply(rno)
{
    const response = await axios.get(`/replies/${rno}`) // 댓글 데이터를 받아옴
    return response.data
}

//특정 댓글을 수정
async function modifyReply(replyObj)
{
    const response = await axios.put(`/replies/${replyObj.rno}`, replyObj) // 특정 댓글 내용을 업데이트
    return response.data
}

// 특정 댓글을 삭제
async function removeReply(rno)
{
    const response = await axios.delete(`/replies/${rno}`)
    return response.data
}
