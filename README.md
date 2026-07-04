一、项目概述
项目业务背景为基于Android平台的个人旅行日记应用“途易助手”的开发，在当今快节奏的生活中，人们经常外出旅行，希望能够记录下旅途中的美好时光、重要地点和感受，传统的纸质日记或简单的照片存储方式已经无法满足现代用户的需求，他们需要一个功能完整、界面友好的数字化解决方案来整理和保存旅行记忆；该应用采用现代化的Android开发技术栈，包括Jetpack Compose、ViewModel、Navigation、Retrofit、Room和DataStore等组件，构建了一个功能完整的旅行记录应用，且项目作为安卓开发期末项目，展示了现代Android应用开发的最佳实践和技术整合能力。
该项目的业务需求可通过代码分析明确，核心功能需求上，用户可以创建、编辑、删除旅行记录，包括标题、描述、日期等基本信息，同时支持添加旅行照片，提供拍照和从相册选择的功能，能够记录旅行中的具体位置信息且可能集成地图服务，所有旅行记录还需要安全地存储在本地数据库中以确保数据不会丢失；用户体验需求方面，应用采用现代化的Jetpack Compose框架，提供流畅、美观的直观用户界面，通过Navigation组件实现页面间的无缝跳转，支持用户按关键词搜索历史旅行记录，还具备深色主题等个性化设置功能；技术架构需求上，采用MVVM架构模式实现UI层、业务逻辑层和数据层的清晰分离，使用StateFlow和协程实现响应式UI更新，通过Retrofit集成地理编码等网络服务，借助Hilt实现依赖注入以提高代码的可维护性，使用Room数据库存储旅行数据、DataStore存储用户偏好；性能和安全需求则要求本地数据存储安全可靠以防止数据泄露，应用需要快速响应并提供流畅的用户体验，同时要合理管理内存使用，避免内存泄漏。
该项目具备多方面的重要意义，技术学习意义上，项目完整实现了现代Android开发的推荐架构模式，包括MVVM、响应式编程等，成功整合了Jetpack Compose、Room、Retrofit、Hilt等主流技术组件，遵循Android开发最佳实践，为学习者提供了完整的参考示例；实用价值方面，它为用户提供了一个完整的旅行记忆管理解决方案，满足了现代人记录生活、整理回忆的实际需求，帮助用户系统化地管理旅行相关的文字、图片和位置信息；教育价值上，作为安卓开发期末项目，它全面展示了学生对Android开发技术的掌握程度，验证了开发者对现代Android开发技术栈的理解和应用能力，体现了软件工程中的架构设计、模块化开发等重要概念；创新意义方面，通过现代化的UI设计和流畅的交互提升了旅行记录的体验，将文字记录、图片存储、位置信息等多种功能整合到一个应用中，且采用Jetpack Compose等新技术，体现了对未来Android开发趋势的把握。
二、期末项目开发环境
    本项目采用JAVA语言及第三方库进行设计与开发，项目基于kotlin+java进行构建，其软件开发环境为：
1.软件开发环境：
      Kotlin 1.9+Android Studio 2023.2.1+Gradle 8.13  
2.数据库管理系统：
   本项目数据库管理系统采用Room（SQLite），内设 2 个表，各表的结构如下：
   1. trips表：
      - id: Long (主键，自动生成)
      - title: String (标题)
      - description: String (描述)
      - date: String (日期)
      - photo_path: String? (照片路径，可为空)
      - created_at: Long (创建时间，默认当前时间戳)
      - updated_at: Long (更新时间，默认当前时间戳)

   2. locations表：
      - id: Long (主键，自动生成)
      - trip_id: Long (外键，关联trips表)
      - name: String (位置名称)
      - latitude: Double (纬度)
      - longitude: Double (经度)
      - address: String (地址)
      - created_at: Long (创建时间，默认当前时间戳)
      - updated_at: Long (更新时间，默认当前时间戳)


3.第三方库包
 UI和框架库
   - androidx.activity:activity-compose - Compose与Activity集成
   - androidx.compose.ui:ui - Jetpack Compose基础UI组件
   - androidx.compose.ui:ui-tooling-preview - Compose预览工具
   - androidx.compose.material3:material3 - Material Design 3组件
   - androidx.navigation:navigation-compose - Compose导航组件

  数据库和存储库
   - androidx.room:room-runtime - Room数据库运行时
   - androidx.room:room-ktx - Room协程支持
   - androidx.datastore:datastore-preferences - DataStore偏好设置

  网络和数据处理库
   - com.squareup.retrofit2:retrofit - HTTP客户端库
   - com.squareup.retrofit2:converter-moshi - Retrofit Moshi转换器
   - com.squareup.okhttp3:logging-interceptor - HTTP日志拦截器
   - com.squareup.moshi:moshi - JSON解析库
   - io.coil-kt:coil-compose - Compose图像加载库
   - io.coil-kt:coil-svg - SVG图像支持

  依赖注入库
   - com.google.dagger:hilt-android - Hilt依赖注入框架
   - androidx.hilt:hilt-navigation-compose - Hilt与Compose导航集成

  其他库
   - com.google.android.material:material - Material Design组件
   - com.google.android.gms:play-services-location - 位置服务
   - androidx.compose.foundation:foundation - Compose基础组件（瀑布流布局）

  代码生成库
   - com.squareup.moshi:moshi-kotlin-codegen - Moshi代码生成（编译时）

  注解处理器
   - androidx.room:room-compiler - Room编译器
   - com.google.dagger:hilt-compiler - Hilt编译器
   - com.google.devtools.ksp - Kotlin符号处理插件
 三、期末项目成果演示
详细写出各功能模块的实现过程并截图。
- 1.首页（旅行日志列表）
   使用本系统时，打开即可显示首页，首页界面如图1，图2所示。
<img width="324" height="716" alt="image" src="https://github.com/user-attachments/assets/6a5f3d03-3296-4555-b973-56648b2048ce" />
<img width="332" height="736" alt="image" src="https://github.com/user-attachments/assets/fd35346f-731a-4080-9d34-e1e83dccdd84" />
- 2.添加页
   使用本系统时，打开后点击底部导航栏中间加号即可显示添加页，添加页界面如图1所示。
<img width="266" height="592" alt="image" src="https://github.com/user-attachments/assets/c7da8df0-0862-40d4-ada7-cda2ab4873aa" />
- 3.编辑页
   使用本系统时，打开后点击单个卡片进入详细页点击右上角修改按钮即可显示编辑页，编辑页界面如图1所示。
<img width="234" height="520" alt="image" src="https://github.com/user-attachments/assets/745d8892-b8a4-4b31-bae6-051161b62575" />
- 4.汇率计算器页
   使用本系统时，打开后点击底部导航栏处汇率按钮即可显示汇率页，汇率页界面如图1所示。
<img width="234" height="520" alt="image" src="https://github.com/user-attachments/assets/fe502c5b-5b61-4589-94a7-97dd5c6f5a63" />
- 5.翻译页
   使用本系统时，打开后点击底部导航栏处翻译按钮即可显示翻译页，翻译页界面如图1所示。
<img width="234" height="520" alt="image" src="https://github.com/user-attachments/assets/be1ed991-c0c4-4233-9dd5-7b614764733f" />
- 6.天气页
   使用本系统时，打开后点击底部导航栏处天气按钮即可显示天气页，天气页界面如图1,图2所示。
<img width="254" height="562" alt="image" src="https://github.com/user-attachments/assets/1a28f564-446a-43aa-ba11-51fce44fa797" />
<img width="252" height="560" alt="image" src="https://github.com/user-attachments/assets/12076a76-3e1d-4199-98e4-ad48d9a552d2" />


















