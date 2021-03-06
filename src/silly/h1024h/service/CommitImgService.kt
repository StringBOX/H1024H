package silly.h1024h.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import silly.h1024h.common.Config
import silly.h1024h.common.Config.RES_PATH
import silly.h1024h.common.Config.SERVICE_URL
import silly.h1024h.dao.ImgResDao
import silly.h1024h.entity.ImgRes
import silly.h1024h.service.impl.CommitImgServiceImpl
import silly.h1024h.service.impl.UpdateServiceImpl
import silly.h1024h.utils.FileUtil
import silly.h1024h.utils.Util
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.reflect.Type
import javax.servlet.http.HttpServletRequest

class CommitImgService : CommitImgServiceImpl {

    override fun isHaveCover(irDetails: String): Boolean {
        return imgResDao.findByIrCoverIrDetails(1, irDetails).isNotEmpty()
    }

    private val imgResDao = ImgResDao()

    override fun saveImg(imgRes: ImgRes): Boolean {
        val findByDetails = imgResDao.findByDetails(imgRes.irDetails)
        // 判断是否有同组
        var irType = if (findByDetails.isEmpty()) {
            // 获取组的最大值
            val irTypeMax = imgResDao.getIrTypeMax()
            if (irTypeMax.isNotEmpty()) {
                // 生成新的组id
                irTypeMax[0].irType + 1
            } else {
                // 第一次提交图片
                1
            }
        } else {
            // 使用这一组
            findByDetails[0].irType
        }
        // url json -> list
        val urlList = Gson().fromJson<List<String>>(imgRes.urlJson, object : TypeToken<List<String>>() {}.type)
        val imgResList = arrayListOf<ImgRes>()

        for (url in urlList) {
            // 判断中是设置封面
            val imgResC = ImgRes(url, irType, if (url == imgRes.irUrl) 1 else 0, imgRes.irDetails)
            val irUrl = imgResC.irUrl
            imgResC.irUrl = irUrl.replace("/uploadfile", "/res/img")
            if (!File("$RES_PATH/res/img").exists()) File("$RES_PATH/res/img").mkdirs()// 创建文件夹
            if (FileUtil.cutFile(RES_PATH + irUrl, RES_PATH + imgResC.irUrl)) imgResList.add(imgResC)
        }
        return imgResDao.saveImg(imgResList)
    }
}