package com.carlo.vault

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContent { CarloVaultApp() }
    }
}

private enum class Screen(val label: String, val icon: ImageVector) {
    Cockpit("Cockpit", Icons.Rounded.Search),
    Vault("Vault", Icons.Rounded.Lock),
    Generator("Genera", Icons.Rounded.Bolt),
    Control("Flag", Icons.Rounded.Settings)
}

private data class VaultItem(
    val service: String,
    val login: String,
    val category: String,
    val strength: Int,
    val flags: List<String>,
    val accent: Color
)

private data class SecurityFlag(
    val title: String,
    val detail: String,
    val icon: ImageVector,
    val enabled: Boolean,
    val critical: Boolean = false
)

private val Ink = Color(0xFF06121A)
private val Panel = Color(0xFF0E2027)
private val PanelLight = Color(0xFF142D34)
private val Mint = Color(0xFF00A896)
private val Sun = Color(0xFFFFB703)
private val Coral = Color(0xFFFF6B5F)
private val Ice = Color(0xFFF7F9F4)
private val Blue = Color(0xFF4CC9F0)

@Composable
private fun CarloVaultApp() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Ink,
            surface = Panel,
            primary = Mint,
            secondary = Sun,
            tertiary = Blue,
            onBackground = Ice,
            onSurface = Ice,
            onPrimary = Ink
        )
    ) {
        var screen by remember { mutableStateOf(Screen.Cockpit) }
        val vault = remember { sampleVault() }
        val flags = remember { mutableStateMapOf<String, Boolean>() }
        sampleFlags().forEach { flags.putIfAbsent(it.title, it.enabled) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF031015), Color(0xFF0B1F26), Color(0xFF101417))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
                    .padding(top = 30.dp, bottom = 16.dp)
            ) {
                Header()
                Spacer(Modifier.height(18.dp))
                NavigationRail(screen, onSelect = { screen = it })
                Spacer(Modifier.height(18.dp))
                AnimatedContent(targetState = screen, label = "screen") { target ->
                    when (target) {
                        Screen.Cockpit -> Cockpit(vault, flags.values.count { it })
                        Screen.Vault -> Vault(vault)
                        Screen.Generator -> Generator()
                        Screen.Control -> ControlCenter(flags)
                    }
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Mint, Color(0xFF07545C))))
        ) {
            Icon(Icons.Rounded.Security, null, tint = Ink, modifier = Modifier.align(Alignment.Center))
        }
        Column(Modifier.padding(start = 12.dp).weight(1f)) {
            Text("CarloVault", fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text("Password manager ad assetto zero-trust", color = Ice.copy(alpha = .68f), fontSize = 13.sp)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Rounded.Add, contentDescription = "Nuova voce", tint = Sun)
        }
    }
}

@Composable
private fun NavigationRail(selected: Screen, onSelect: (Screen) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Panel.copy(alpha = .92f))
            .padding(6.dp)
    ) {
        Screen.entries.forEach { item ->
            val active = item == selected
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelect(item) }
                    .background(if (active) Mint else Color.Transparent)
                    .padding(horizontal = 7.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(item.icon, null, tint = if (active) Ink else Ice.copy(alpha = .78f), modifier = Modifier.size(18.dp))
                AnimatedVisibility(active) {
                    Text(
                        item.label,
                        color = Ink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 5.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun Cockpit(vault: List<VaultItem>, activeFlags: Int) {
    val score = vault.map { it.strength }.average().roundToInt()
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                SecurityScore(score, Modifier.weight(1.05f))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(.95f)) {
                    Metric("Credenziali", vault.size.toString(), "tutte cifrate")
                    Metric("Flag attivi", activeFlags.toString(), "policy operative")
                }
            }
        }
        item {
            SectionTitle("Rischi prioritari")
            RiskStrip("3 password riutilizzate", "Ruota ora gli account social", Coral, .74f)
            RiskStrip("2 account senza 2FA", "Banca e cloud storage", Sun, .51f)
            RiskStrip("1 dominio sospetto", "Login simile a servizio reale", Blue, .32f)
        }
        item {
            SectionTitle("Azioni rapide")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                ActionButton("Audit", Icons.Rounded.Search, Modifier.weight(1f))
                ActionButton("Condividi", Icons.Rounded.Key, Modifier.weight(1f))
                ActionButton("Lockdown", Icons.Rounded.Security, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SecurityScore(score: Int, modifier: Modifier = Modifier) {
    val animated by animateFloatAsState(score / 100f, label = "score")
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Panel.copy(alpha = .96f),
        modifier = modifier.height(190.dp)
    ) {
        Box(Modifier.padding(18.dp)) {
            Canvas(Modifier.size(132.dp).align(Alignment.Center)) {
                drawCircle(Color.White.copy(alpha = .07f), radius = size.minDimension / 2, center = center, style = Stroke(18.dp.toPx()))
                drawArc(
                    brush = Brush.sweepGradient(listOf(Coral, Sun, Mint, Coral)),
                    startAngle = -90f,
                    sweepAngle = 360f * animated,
                    useCenter = false,
                    style = Stroke(18.dp.toPx(), cap = StrokeCap.Round)
                )
                drawCircle(Mint.copy(alpha = .16f), radius = 12.dp.toPx(), center = Offset(size.width * .78f, size.height * .18f))
            }
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(score.toString(), fontSize = 34.sp, fontWeight = FontWeight.Black)
                Text("score", color = Ice.copy(alpha = .62f), fontSize = 12.sp)
            }
            Text("Security cockpit", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopStart))
            Text("Live", color = Mint, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}

@Composable
private fun Metric(valueLabel: String, value: String, note: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = PanelLight.copy(alpha = .88f), modifier = Modifier.fillMaxWidth().height(89.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.Center) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Sun)
            Text(valueLabel, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(note, color = Ice.copy(alpha = .58f), fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable
private fun RiskStrip(title: String, detail: String, color: Color, progress: Float) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Panel)
            .border(1.dp, color.copy(alpha = .45f), RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Warning, null, tint = color, modifier = Modifier.size(20.dp))
            Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp).weight(1f))
        }
        Text(detail, color = Ice.copy(alpha = .62f), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 9.dp))
        LinearProgressIndicator(progress = { progress }, color = color, trackColor = Color.White.copy(alpha = .08f), modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape))
    }
}

@Composable
private fun ActionButton(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(containerColor = PanelLight, contentColor = Ice),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.height(52.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(18.dp))
        Text(text, modifier = Modifier.padding(start = 6.dp), fontSize = 12.sp, maxLines = 1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Vault(vault: List<VaultItem>) {
    var search by remember { mutableStateOf(TextFieldValue("")) }
    var category by remember { mutableStateOf("Tutti") }
    val categories = listOf("Tutti") + vault.map { it.category }.distinct()
    val filtered = vault.filter {
        (category == "Tutti" || it.category == category) &&
            (search.text.isBlank() || it.service.contains(search.text, true) || it.login.contains(search.text, true))
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                placeholder = { Text("Cerca account, dominio o username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { chip ->
                    FilterChip(selected = category == chip, onClick = { category = chip }, label = { Text(chip) })
                }
            }
        }
        items(filtered, key = { it.service }) { item -> VaultRow(item) }
    }
}

@Composable
private fun VaultRow(item: VaultItem) {
    Surface(shape = RoundedCornerShape(8.dp), color = Panel, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(item.accent.copy(alpha = .22f))
                ) {
                    Text(item.service.first().toString(), color = item.accent, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.Center))
                }
                Column(Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(item.service, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(item.login, color = Ice.copy(alpha = .58f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text("${item.strength}%", color = if (item.strength > 80) Mint else if (item.strength > 60) Sun else Coral, fontWeight = FontWeight.Black)
            }
            LinearProgressIndicator(
                progress = { item.strength / 100f },
                color = if (item.strength > 80) Mint else if (item.strength > 60) Sun else Coral,
                trackColor = Color.White.copy(alpha = .08f),
                modifier = Modifier.padding(top = 12.dp).fillMaxWidth().height(5.dp).clip(CircleShape)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.padding(top = 10.dp)) {
                item.flags.take(3).forEach { Badge(it) }
            }
        }
    }
}

@Composable
private fun Generator() {
    var length by remember { mutableFloatStateOf(24f) }
    var symbols by remember { mutableStateOf(true) }
    var readable by remember { mutableStateOf(false) }
    var passphrase by remember { mutableStateOf(false) }
    var oneTime by remember { mutableStateOf(true) }
    val preview = if (passphrase) "orbit-river-copper-matrix-${length.roundToInt()}" else "N9!vR2#qL7@zP4%xW6&s"

    LazyColumn(verticalArrangement = Arrangement.spacedBy(13.dp), modifier = Modifier.fillMaxSize()) {
        item {
            Surface(shape = RoundedCornerShape(8.dp), color = Panel, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text("Password pronta", color = Ice.copy(alpha = .62f), fontSize = 12.sp)
                    Text(preview, fontSize = 22.sp, fontWeight = FontWeight.Black, maxLines = 2)
                    Row(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = {}, shape = RoundedCornerShape(8.dp)) {
                            Icon(Icons.Rounded.ContentCopy, null, modifier = Modifier.size(18.dp))
                            Text("Copia", modifier = Modifier.padding(start = 8.dp))
                        }
                        TextButton(onClick = {}) { Text("Salva nel vault") }
                    }
                }
            }
        }
        item {
            GeneratorSlider("Lunghezza", length.roundToInt().toString(), length) { length = it }
        }
        item { ToggleRow("Simboli speciali", "Aggiunge entropia contro brute force", symbols) { symbols = it } }
        item { ToggleRow("Leggibile", "Evita caratteri ambigui come O, 0, l, 1", readable) { readable = it } }
        item { ToggleRow("Passphrase", "Frasi lunghe ad alta memorabilita", passphrase) { passphrase = it } }
        item { ToggleRow("Link una tantum", "Condivisione con scadenza e revoca", oneTime) { oneTime = it } }
    }
}

@Composable
private fun GeneratorSlider(label: String, value: String, sliderValue: Float, onValue: (Float) -> Unit) {
    Surface(shape = RoundedCornerShape(8.dp), color = Panel, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(value, color = Sun, fontWeight = FontWeight.Black)
            }
            Slider(value = sliderValue, onValueChange = onValue, valueRange = 8f..64f, steps = 55)
        }
    }
}

@Composable
private fun ControlCenter(flags: MutableMap<String, Boolean>) {
    val models = sampleFlags()
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionTitle("Centro flag")
            Text(
                "Policy granulari per blocco, privacy, audit, recovery e condivisione.",
                color = Ice.copy(alpha = .62f),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(models, key = { it.title }) { flag ->
            val checked = flags[flag.title] ?: flag.enabled
            ToggleRow(
                title = flag.title,
                detail = flag.detail,
                checked = checked,
                icon = flag.icon,
                warning = flag.critical,
                onChecked = { flags[flag.title] = it }
            )
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    detail: String,
    checked: Boolean,
    icon: ImageVector = Icons.Rounded.CheckCircle,
    warning: Boolean = false,
    onChecked: (Boolean) -> Unit
) {
    Surface(shape = RoundedCornerShape(8.dp), color = Panel, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background((if (warning) Coral else Mint).copy(alpha = .16f))
            ) {
                Icon(icon, null, tint = if (warning) Coral else Mint, modifier = Modifier.align(Alignment.Center).size(20.dp))
            }
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(detail, color = Ice.copy(alpha = .58f), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Switch(checked = checked, onCheckedChange = onChecked)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 2.dp, bottom = 4.dp))
}

@Composable
private fun Badge(text: String) {
    Text(
        text,
        color = Ice.copy(alpha = .78f),
        fontSize = 11.sp,
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = .08f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

private fun sampleVault() = listOf(
    VaultItem("Google Workspace", "carlo@azienda.it", "Lavoro", 94, listOf("2FA", "Passkey", "No breach"), Mint),
    VaultItem("Banca Aurora", "c.amatori", "Finanza", 88, listOf("Hardware key", "Auto-lock"), Blue),
    VaultItem("GitHub", "carlo-dev", "Dev", 79, listOf("Token", "Da ruotare"), Sun),
    VaultItem("Netflix", "famiglia@home.it", "Casa", 52, listOf("Riutilizzata", "Debole"), Coral),
    VaultItem("Dropbox", "archive@cloud.it", "Cloud", 68, listOf("Senza 2FA", "Export lock"), Sun),
    VaultItem("Instagram", "carlo.private", "Social", 47, listOf("Phishing watch", "Riutilizzata"), Coral)
)

private fun sampleFlags() = listOf(
    SecurityFlag("Sblocco biometrico", "Fingerprint o volto dopo master password", Icons.Rounded.Fingerprint, true),
    SecurityFlag("Passkey-first", "Preferisce passkey a password classiche", Icons.Rounded.Key, true),
    SecurityFlag("Screen shield", "Blocca screenshot e app switcher preview", Icons.Rounded.Shield, true),
    SecurityFlag("Auto-lock dinamico", "Blocca per inattivita, movimento o rete non fidata", Icons.Rounded.Lock, true),
    SecurityFlag("Clipboard wipe", "Cancella appunti dopo 20 secondi", Icons.Rounded.ContentCopy, true),
    SecurityFlag("Audit breach", "Segnala leak, password deboli e riutilizzi", Icons.Rounded.Search, true),
    SecurityFlag("2FA obbligatoria", "Mostra priorita alta sugli account senza secondo fattore", Icons.Rounded.Security, true),
    SecurityFlag("Vault esca", "Profilo decoy con credenziali innocue", Icons.Rounded.Security, false),
    SecurityFlag("Travel mode", "Nasconde categorie sensibili durante i viaggi", Icons.Rounded.Lock, false),
    SecurityFlag("Emergency access", "Recupero fiduciario con finestra di attesa", Icons.Rounded.Warning, false, critical = true),
    SecurityFlag("Backup cifrato", "Esporta solo archivi protetti e verificabili", Icons.Rounded.Lock, true),
    SecurityFlag("Offline mode", "Nessuna sincronizzazione finche resta attivo", Icons.Rounded.Bolt, false),
    SecurityFlag("Phishing guard", "Confronta dominio, lookalike e redirect", Icons.Rounded.Search, true),
    SecurityFlag("Cronologia password", "Conserva versioni precedenti cifrate", Icons.Rounded.CheckCircle, true),
    SecurityFlag("Condivisione approvata", "Richiede scadenza, ruolo e revoca", Icons.Rounded.Key, true)
)
