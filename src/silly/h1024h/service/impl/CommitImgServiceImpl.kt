package silly.h1024h.service.impl

import silly.h1024h.entity.ImgRes
import javax.servlet.http.HttpServletRequest

interface CommitImgServiceImpl {
    /**
     * 存储图片
     */
    fun saveImg(imgRes: ImgRes):Boolean

    fun isHaveCover(irDetails: String):Boolean
}