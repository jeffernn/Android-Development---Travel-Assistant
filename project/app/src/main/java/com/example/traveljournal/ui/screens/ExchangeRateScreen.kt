package com.example.traveljournal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.traveljournal.data.model.ExchangeRate
import com.example.traveljournal.viewmodel.ExchangeRateViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateScreen(
    viewModel: ExchangeRateViewModel = hiltViewModel()
) {
    val exchangeRates by viewModel.exchangeRates.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchExchangeRates()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "汇率计算器",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "加载汇率失败: $error",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                ExchangeRateCalculator(
                    exchangeRates = exchangeRates
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateCalculator(
    exchangeRates: List<ExchangeRate>
) {
    var amount by remember { mutableStateOf(TextFieldValue("1")) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCurrency by remember {
        mutableStateOf(exchangeRates.find { it.currency == "USD" } ?: exchangeRates.firstOrNull() ?: ExchangeRate("CNY", 1.0))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 金额输入框
        OutlinedTextField(
            value = amount,
            onValueChange = {
                if (it.text.isEmpty() || it.text.matches(Regex("^\\d*\\.?\\d*$"))) {
                    amount = it
                }
            },
            label = { Text("输入人民币金额") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // 货币选择下拉框
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                value = "${selectedCurrency.currency} (${getCurrencyName(selectedCurrency.currency)})",
                onValueChange = { },
                label = { Text("选择目标货币") },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                exchangeRates.filter { it.currency != "CNY" }.forEach { rate ->
                    DropdownMenuItem(
                        text = { Text("${rate.currency} (${getCurrencyName(rate.currency)})") },
                        onClick = {
                            selectedCurrency = rate
                            expanded = false
                        }
                    )
                }
            }
        }

        // 显示转换结果 - 使用更现代化的卡片设计
        val convertedAmount = (amount.text.toDoubleOrNull() ?: 0.0) * selectedCurrency.rate
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "转换结果",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "%.4f 人民币 = %.4f %s".format(
                        amount.text.toDoubleOrNull() ?: 0.0,
                        convertedAmount,
                        selectedCurrency.currency
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = "所有货币汇率:",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(exchangeRates.filter { it.currency != "CNY" }) { rate ->
                val convertedAmount = (amount.text.toDoubleOrNull() ?: 0.0) * rate.rate
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCurrency = rate },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${rate.currency} (${getCurrencyName(rate.currency)})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "汇率: ${rate.rate}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "%.4f".format(convertedAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// 获取货币名称的辅助函数
fun getCurrencyName(currencyCode: String): String {
    return when (currencyCode) {
        "USD" -> "美元"
        "EUR" -> "欧元"
        "CNY" -> "人民币"
        "JPY" -> "日元"
        "GBP" -> "英镑"
        "HKD" -> "港币"
        "CAD" -> "加拿大元"
        "AUD" -> "澳大利亚元"
        "CHF" -> "瑞士法郎"
        "SGD" -> "新加坡元"
        "KRW" -> "韩元"
        "INR" -> "印度卢比"
        "ZAR" -> "南非兰特"
        "TWD" -> "新台币"
        "THB" -> "泰铢"
        "MOP" -> "澳门元"
        "KPW" -> "朝鲜元"
        "NZD" -> "新西兰元"
        "AED" -> "阿联酋迪拉姆"
        "AFN" -> "阿富汗尼"
        "ALL" -> "阿尔巴尼列克"
        "AMD" -> "亚美尼亚德拉姆"
        "ANG" -> "荷兰盾"
        "AOA" -> "安哥拉宽扎"
        "ARS" -> "阿根廷比索"
        "AWG" -> "阿鲁巴弗罗林"
        "AZN" -> "阿塞拜疆马纳特"
        "BAM" -> "波黑可兑换马克"
        "BBD" -> "巴巴多斯元"
        "BDT" -> "孟加拉国塔卡"
        "BGN" -> "保加利亚列弗"
        "BHD" -> "巴林第纳尔"
        "BIF" -> "布隆迪法郎"
        "BMD" -> "百慕大元"
        "BND" -> "文莱元"
        "BOB" -> "玻利维亚诺"
        "BRL" -> "巴西雷亚尔"
        "BSD" -> "巴哈马元"
        "BTN" -> "不丹努扎姆"
        "BWP" -> "博茨瓦纳普拉"
        "BYN" -> "白俄罗斯卢布"
        "BZD" -> "伯利兹元"
        "CDF" -> "刚果法郎"
        "CLP" -> "智利比索"
        "COP" -> "哥伦比亚比索"
        "CRC" -> "哥斯达黎加科朗"
        "CUP" -> "古巴比索"
        "CVE" -> "佛得角埃斯库多"
        "CZK" -> "捷克克朗"
        "DJF" -> "吉布提法郎"
        "DKK" -> "丹麦克朗"
        "DOP" -> "多米尼加比索"
        "DZD" -> "阿尔及利亚第纳尔"
        "EGP" -> "埃及镑"
        "ERN" -> "厄立特里亚纳克法"
        "ETB" -> "埃塞俄比亚比尔"
        "FJD" -> "斐济元"
        "FKP" -> "福克兰群岛镑"
        "GEL" -> "格鲁吉亚拉里"
        "GHS" -> "加纳塞地"
        "GIP" -> "直布罗陀镑"
        "GMD" -> "冈比亚达拉西"
        "GNF" -> "几内亚法郎"
        "GTQ" -> "危地马拉格查尔"
        "GYD" -> "圭亚那元"
        "HNL" -> "洪都拉斯伦皮拉"
        "HRK" -> "克罗地亚库纳"
        "HTG" -> "海地古德"
        "HUF" -> "匈牙利福林"
        "IDR" -> "印度尼西亚卢比"
        "ILS" -> "以色列新谢克尔"
        "IQD" -> "伊拉克第纳尔"
        "IRR" -> "伊朗里亚尔"
        "ISK" -> "冰岛克郎"
        "JMD" -> "牙买加元"
        "JOD" -> "约旦第纳尔"
        "KES" -> "肯尼亚先令"
        "KGS" -> "吉尔吉斯斯坦索姆"
        "KHR" -> "柬埔寨瑞尔"
        "KMF" -> "科摩罗法郎"
        "KWD" -> "科威特第纳尔"
        "KYD" -> "开曼群岛元"
        "KZT" -> "哈萨克斯坦坚戈"
        "LAK" -> "老挝基普"
        "LBP" -> "黎巴嫩镑"
        "LKR" -> "斯里兰卡卢比"
        "LRD" -> "利比里亚元"
        "LSL" -> "莱索托洛蒂"
        "LYD" -> "利比亚第纳尔"
        "MAD" -> "摩洛哥迪拉姆"
        "MDL" -> "摩尔多瓦列伊"
        "MGA" -> "马达加斯加阿里亚里"
        "MKD" -> "马其顿代纳尔"
        "MMK" -> "缅甸元"
        "MNT" -> "蒙古图格里克"
        "MRU" -> "乌吉亚"
        "MUR" -> "毛里求斯卢比"
        "MVR" -> "马尔代夫拉菲亚"
        "MWK" -> "马拉维克瓦查"
        "MXN" -> "墨西哥比索"
        "MYR" -> "林吉特"
        "MZN" -> "莫桑比克新梅蒂卡尔"
        "NAD" -> "纳米比亚元"
        "NGN" -> "尼日利亚奈拉"
        "NIO" -> "尼加拉瓜新科多巴"
        "NOK" -> "挪威克朗"
        "NPR" -> "尼泊尔卢比"
        "OMR" -> "阿曼里亚尔"
        "PAB" -> "巴拿马巴波亚"
        "PEN" -> "秘鲁新索尔"
        "PGK" -> "巴布亚新几内亚基那"
        "PHP" -> "菲律宾比索"
        "PKR" -> "巴基斯坦卢比"
        "PLN" -> "波兰兹罗提"
        "PYG" -> "巴拉圭瓜拉尼"
        "QAR" -> "卡塔尔里亚尔"
        "RON" -> "罗马尼亚列伊"
        "RSD" -> "塞尔维亚第纳尔"
        "RUB" -> "俄罗斯卢布"
        "RWF" -> "卢旺达法郎"
        "SAR" -> "沙特里亚尔"
        "SBD" -> "所罗门群岛元"
        "SCR" -> "塞舌尔卢比"
        "SDG" -> "苏丹镑"
        "SEK" -> "瑞典克朗"
        "SHP" -> "圣赫勒拿镑"
        "SLL" -> "塞拉利昂利昂"
        "SOS" -> "索马里先令"
        "SRD" -> "苏里南元"
        "SSP" -> "南苏丹镑"
        "STN" -> "多布拉"
        "SYP" -> "叙利亚镑"
        "SZL" -> "斯威士兰里兰吉尼"
        "TJS" -> "塔吉克斯坦索莫尼"
        "TMT" -> "土库曼斯坦马纳特"
        "TND" -> "突尼斯第纳尔"
        "TOP" -> "汤加潘加"
        "TRY" -> "土耳其里拉"
        "TTD" -> "特立尼达多巴哥元"
        "TZS" -> "坦桑尼亚先令"
        "UAH" -> "乌克兰格里夫纳"
        "UGX" -> "乌干达先令"
        "UYU" -> "乌拉圭比索"
        "UZS" -> "乌兹别克斯坦苏姆"
        "VND" -> "越南盾"
        "VUV" -> "瓦努阿图瓦图"
        "WST" -> "萨摩亚塔拉"
        "XAF" -> "中非法郎"
        "XCD" -> "东加勒比元"
        "XOF" -> "西非法郎"
        "XPF" -> "太平洋法郎"
        "YER" -> "也门里亚尔"
        "ZMW" -> "赞比亚克瓦查"
        else -> "货币"
    }
}