(ns piaojuocr.gui
  (:require [seesaw.core :as gui]
            [seesaw.icon :as icon]
            [seesaw.bind :as bind]
            [seesaw.mig :refer [mig-panel]]
            [piaojuocr.viewer :as iviewer]
            [piaojuocr.logging :as logging]
            [piaojuocr.setting :as setting]
            [piaojuocr.config :as config]
            [piaojuocr.theme :as theme]
            [piaojuocr.ocr :as ocr]
            [taoensso.timbre :as log]
            [piaojuocr.img :as img]))

(defn a-open [e]
  (when-let [f (iviewer/choose-pic)]
    (log/debug "open new image file:" f)
    (let [root (gui/to-root e)
          img (iviewer/read-image f)]
      (iviewer/set-image! root :main-image img))))

(defn a-save [e]
  (when-let [f (iviewer/choose-pic :save)]
    (log/debug "saving image file:" f)
    (iviewer/save-image (gui/to-root e)
                        :main-image
                        f)))

(defn a-exit  [e] (gui/dispose! e))

(def curr-img (atom nil))

(defn a-pic-edit [e]
  (let [root (gui/to-root e)]
    (if-let [img (iviewer/get-image root :main-image)]
      (let [img (img/show-image "编辑图片" img)]
        (log/debug "edit pic.")
        (reset! curr-img img))
      (gui/alert "还没有打开文件"))))

(def auto-update (atom (config/get-config :auto-update true)))

(defn make-imagej-updated-cb [root]
  (fn [imp]
    (when @auto-update
      (log/info "image updated.")
      (gui/invoke-later
       (->> (img/get-image imp)
            (iviewer/set-image! root :main-image))))))

(defn a-pic-update [e]
  (if-let [img @curr-img]
    (->> (img/get-image img)
         (iviewer/set-image! (gui/to-root e)
                             :main-image))
    (gui/alert "没有编辑的图片")))

(defn make-menus []
  (let [a-open (gui/action :handler a-open :name "打开" :tip "打开图片文件" :key "menu O")
        a-save (gui/action :handler a-save :name "保存" :tip "保存当前图片" :key "menu S")
        a-exit (gui/action :handler a-exit :name"退出" :tip "退出程序" :key "menu X")
        a-pic-edit (gui/action :handler a-pic-edit :name "编辑图片" :tip "使用ImageJ编辑图片" :key "menu E")
        a-pic-update (gui/action :handler a-pic-update :name "更新图片" :tip "更新显示编辑后的图片")
        a-pic-auto-update (gui/checkbox-menu-item :text "自动更新图片"
                                                  :selected? @auto-update)]
    (bind/bind
     (bind/property a-pic-auto-update :selected?)
     auto-update)
    (gui/menubar
     :items [(gui/menu :text "文件" :items [a-open a-save a-exit])
             (gui/menu :text "图片" :items [a-pic-edit a-pic-update a-pic-auto-update])])))

(defn make-pic-ocr-view [frame]
  (let [img-panel (iviewer/make-pic-viewer :main-image)]
    (gui/border-panel
     :center img-panel)))

(defn add-behaviors
  [root]
  )

(defn make-main-view [frame]
  (gui/tabbed-panel :placement :top :overflow :scroll
                    :tabs [{:title "图片处理"
                            :tip "图片处理与ocr功能"
                            :content (make-pic-ocr-view frame)}
                           {:title "设置"
                            :tip "系统设置"
                            :content (setting/make-view frame)}
                           {:title "日志"
                            :tip "日志面板"
                            :content (logging/make-view frame)}]))

(defn make-frame []
  (theme/wrap-theme
   (let [f (gui/frame
            :title "文字识别测试"
            :menubar (make-menus))
         cb (img/image-callback {:fn-updated (make-imagej-updated-cb f)})]
     (gui/listen f :window-closing
                 (fn [e]
                   (log/info "close frame window.")
                   (img/remove-image-callback cb)
                   (config/save-config!)
                   (log/info "exit over.")))
     (img/add-image-callback cb)
     (add-behaviors f)
     (gui/config! f :content (make-main-view f)))))

(defn show-frame [f]
  (-> f gui/pack! gui/show!))

(comment
  (def f1 (-> (make-frame)
              show-frame))

  )
