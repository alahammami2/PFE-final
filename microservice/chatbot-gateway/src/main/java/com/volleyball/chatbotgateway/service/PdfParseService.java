package com.volleyball.chatbotgateway.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfParseService {

    /**
     * Placeholder: renvoie un JSON de stats globales vide avec métadonnées.
     */
    public Mono<String> parseStatsFromPdf(byte[] pdfBytes, String teamFilter) {
        String json = "{" +
                "\n  \"match\": {\n    \"uploadedAt\": \"" + Instant.now() + "\"\n  }," +
                "\n  \"team\": \"" + (teamFilter == null ? "ALL" : teamFilter) + "\"," +
                "\n  \"players\": []\n}";
        return Mono.just(json);
    }

    /**
     * Debug helper: returns a JSON matrix of tokens per player row for the COK block.
     * This helps align and finalize exact column mapping.
     */
    public Mono<String> extractCokTokenMatrix(byte[] pdfBytes, String teamFilter) {
        return Mono.fromCallable(() -> {
            String text = loadText(pdfBytes);
            List<String> lines = Arrays.asList(text.split("\r?\n"));
            List<String> block = extractTeamBlock(lines, teamFilter);

            StringBuilder sb = new StringBuilder();
            sb.append("{\n  \"rows\": [\n");

            Pattern rowPat = Pattern.compile("^\\s*(\\d{1,2})\\s+((?:[A-Z])?)\\s*([A-Z][A-Z .'-]+?)\\s+([.\\d].+)$");
            boolean first = true;
            for (String raw : block) {
                Matcher m = rowPat.matcher(raw);
                if (!m.find()) continue;
                String num = m.group(1);
                String role = m.group(2) == null ? "" : m.group(2).trim();
                String name = m.group(3).trim();
                String rest = m.group(4).trim();

                List<String> tokens = new ArrayList<>();
                for (String tk : rest.split("\\s+")) {
                    String t = tk.replace(",", ".").replace("(", "").replace(")", "");
                    if (t.matches("[-+]?\\d+%?")) tokens.add(t);
                    else if (t.equals(".")) tokens.add(t);
                }

                if (!first) sb.append(",\n");
                first = false;
                sb.append("    {\n");
                sb.append("      \"num\": \"").append(num).append("\",\n");
                sb.append("      \"role\": \"").append(role).append("\",\n");
                sb.append("      \"name\": \"").append(name.replace("\\", "\\\\").replace("\"", "\\\"")).append("\",\n");
                sb.append("      \"raw\": \"").append(raw.replace("\\", "\\\\").replace("\"", "\\\"")).append("\",\n");
                sb.append("      \"tokens\": [");
                for (int i = 0; i < tokens.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append('\"').append(tokens.get(i)).append('\"');
                }
                sb.append("]\n    }");
            }

            sb.append("\n  ]\n}");
            return sb.toString();
        });
    }

    // Public: return only the CLUB OLYMPIQUE (COK) table block as cleaned text
    public Mono<String> extractCokBlockText(byte[] fileBytes, String teamFilter) {
        return Mono.fromCallable(() -> {
            String text = loadText(fileBytes);
            List<String> lines = Arrays.asList(text.split("\r?\n"));
            List<String> block = extractTeamBlock(lines, teamFilter);
            if (block.isEmpty()) return "";
            // Clean: drop empty lines, collapse 3+ spaces to 2, trim each line
            StringBuilder sb = new StringBuilder();
            for (String l : block) {
                String t = l.replace('\u00A0', ' ').replaceAll(" {3,}", "  ").trim();
                if (t.isEmpty()) continue;
                sb.append(t).append('\n');
            }
            return sb.toString().trim();
        });
    }

    // Public: return the COK table block formatted as an ASCII table (fixed-width columns)
    public Mono<String> extractCokAsciiTable(byte[] fileBytes, String teamFilter) {
        return Mono.fromCallable(() -> {
            String text = loadText(fileBytes);
            List<String> lines = Arrays.asList(text.split("\r?\n"));
            // Restrict parsing to the COK team block to avoid capturing the opponent table
            List<String> block = extractTeamBlock(lines, teamFilter);
            if (block.isEmpty()) {
                return "CLUB OLYMPIQUE\n" + repeat('-', 80) + "\nNo COK block found.";
            }

            // Locate the section header line (Points | Service | Reception | Attaque | Bk)
            int header1 = -1;
            for (int i = 0; i < block.size(); i++) {
                String ln = normalize(block.get(i));
                if ((ln.contains("POINTS") || ln.contains("POINTS")) && ln.contains("SERVICE") && (ln.contains("RECEPTION") || ln.contains("RÉCEPTION") || ln.contains("RECEPTION"))) {
                    header1 = i;
                    break;
                }
            }
            if (header1 == -1) header1 = 0;

            String hline = normalize(block.get(header1));
            // Fallback if accents differ
            int cPoints = Math.max(0, indexOfIgnoreCase(hline, "POINTS"));
            int cService = indexOfIgnoreCase(hline, "SERVICE");
            int cReception = indexOfIgnoreCase(hline, "RÉCEPTION");
            if (cReception < 0) cReception = indexOfIgnoreCase(hline, "RECEPTION");
            int cAttaque = indexOfIgnoreCase(hline, "ATTAQUE");
            int cBk = indexOfIgnoreCase(hline, "BK");
            // Ensure increasing order and defaults
            cService = cService < 0 ? cPoints + 20 : cService;
            cReception = cReception < 0 ? cService + 20 : cReception;
            cAttaque = cAttaque < 0 ? cReception + 20 : cAttaque;
            cBk = cBk < 0 ? cAttaque + 20 : cBk;

            // Table header for output
            String title = "CLUB OLYMPIQUE";
            String header = String.format(
                    "%-3s %-24s | %-14s | %-13s | %-23s | %-22s | %-6s\n",
                    "#", "Nom", "Points (Tot BP W-L)", "Service (Tot Err Pts)", "Reception (Tot Err Pos% Exc%)", "Attaque (Tot Err Blo Pts Pts%)", "Bk Pts");
            String sep = repeat('-', header.length());

            StringBuilder out = new StringBuilder();
            out.append(title).append('\n');
            out.append(sep).append('\n');
            out.append(header);
            out.append(sep).append('\n');

            // Pattern to split number, optional role, and name, leaving the row tail intact
            Pattern rowPat = Pattern.compile("^\\s*(\\d{1,2})\\s+(?:([A-Z])\\s+)?([A-Z][A-Z .'-]+?)\\s+(.*)$");

            for (int i = header1 + 1; i < block.size(); i++) {
                String raw = block.get(i);
                String ln = normalize(raw);
                if (ln.isEmpty()) continue;
                if (ln.contains("POINTS") && ln.contains("SERVICE")) break; // next table inside block boundary

                Matcher mr = rowPat.matcher(raw);
                if (!mr.find()) continue;
                String num = mr.group(1) == null ? "" : mr.group(1).trim();
                String role = mr.group(2) == null ? "" : mr.group(2).trim();
                String name = mr.group(3).trim();
                String tail = mr.group(4);

                // Build a padded line for safe slicing
                String pad = tail + repeat(' ', Math.max(0, cBk + 10));
                String segPoints = safeSub(pad, cPoints - (3 + name.length()), cService - (3 + name.length()));
                String segService = safeSub(pad, cService - (3 + name.length()), cReception - (3 + name.length()));
                String segReception = safeSub(pad, cReception - (3 + name.length()), cAttaque - (3 + name.length()));
                String segAttaque = safeSub(pad, cAttaque - (3 + name.length()), cBk - (3 + name.length()));
                String segBk = safeSub(pad, cBk - (3 + name.length()), cBk - (3 + name.length()) + 10);

                boolean isLibero = "L".equals(role);

                String pTot = ".", pBP = ".", pWL = ".";
                String sTot = ".", sErr = ".", sPts = ".";
                String rTot = ".", rErr = ".", rPos = ".", rExc = ".";
                String aTot = ".", aErr = ".", aBlo = ".", aPts = ".", aPct = ".";
                String bk = ".";

                if (!isLibero) {
                    List<String> pt = scanNums(segPoints);
                    if (pt.size() >= 1) pTot = pt.get(0);
                    if (pt.size() >= 2) pBP = pt.get(1);
                    if (pt.size() >= 3) pWL = pt.get(2);

                    List<String> sv = scanNums(segService);
                    if (sv.size() >= 1) sTot = sv.get(0);
                    if (sv.size() >= 2) sErr = sv.get(1);
                    if (sv.size() >= 3) sPts = sv.get(2);
                }

                List<String> rc = scanNums(segReception);
                if (rc.size() >= 1) rTot = rc.get(0);
                if (rc.size() >= 2) rErr = rc.get(1);
                // percentages often come next; try to find Pos% then Exc%
                String[] pc = scanPerc(segReception);
                if (pc[0] != null) rPos = pc[0];
                if (pc[1] != null) rExc = pc[1];

                if (!isLibero) {
                    List<String> at = scanNums(segAttaque);
                    if (at.size() >= 1) aTot = at.get(0);
                    if (at.size() >= 2) aErr = at.get(1);
                    if (at.size() >= 3) aBlo = at.get(2);
                    if (at.size() >= 4) aPts = at.get(3);
                    String ap = firstPercent(segAttaque);
                    if (ap != null) aPct = ap;

                    List<String> bkNums = scanNums(segBk);
                    if (!bkNums.isEmpty()) bk = bkNums.get(0);
                }

                out.append(String.format(
                        "%-3s %-24s | %3s %3s %4s | %3s %3s %4s | %3s %3s %4s %5s | %3s %3s %3s %3s %5s | %6s\n",
                        n(num), name,
                        n(pTot), n(pBP), n(pWL),
                        n(sTot), n(sErr), n(sPts),
                        n(rTot), n(rErr), p(rPos), p(rExc),
                        n(aTot), n(aErr), n(aBlo), n(aPts), p(aPct),
                        n(bk)
                ));
            }

            return out.toString();
        });
    }

    private static String n(String s) { return (s == null || s.isBlank()) ? "." : s; }
    private static String p(String s) { return (s == null || s.isBlank()) ? "." : s.replace("%", "") + "%"; }
    private static String pick(List<String> list, int idx) { return idx < list.size() ? list.get(idx) : null; }
    private static String pickPercent(List<String> list, int idx) {
        if (idx >= list.size()) return null;
        String v = list.get(idx);
        return v.endsWith("%") ? v : v + "%";
    }

    private static String repeat(char ch, int len) {
        char[] arr = new char[len];
        Arrays.fill(arr, ch);
        return new String(arr);
    }

    // Normalize string similar to norm(), kept for readability where used
    private static String normalize(String s) {
        return norm(s);
    }

    // Case-insensitive indexOf helper
    private static int indexOfIgnoreCase(String haystack, String needle) {
        if (haystack == null || needle == null) return -1;
        String h = haystack.toUpperCase(Locale.ROOT);
        String n = needle.toUpperCase(Locale.ROOT);
        return h.indexOf(n);
    }

    // Safe substring within bounds; trims result for cleaner token scans
    private static String safeSub(String s, int start, int end) {
        if (s == null) return "";
        int len = s.length();
        int st = Math.max(0, start);
        int en = Math.max(st, Math.min(len, end));
        if (st >= en) return "";
        return s.substring(st, en).replace('\u00A0', ' ').trim();
    }

    // Scan integers within a slice, ignoring dots and percentages
    private static List<String> scanNums(String slice) {
        List<String> out = new ArrayList<>();
        if (slice == null || slice.isEmpty()) return out;
        // Accept optional sign; avoid capturing percents here
        Matcher m = Pattern.compile("[-+]?\\d+").matcher(slice.replace(',', '.'));
        while (m.find()) {
            String tok = m.group();
            out.add(tok);
        }
        return out;
    }

    // Scan up to two percentages from a slice in order of appearance
    private static String[] scanPerc(String slice) {
        String[] res = new String[2];
        if (slice == null) return res;
        Matcher m = Pattern.compile("[-+]?\\d+%").matcher(slice.replace(" ", ""));
        int i = 0;
        while (m.find() && i < 2) {
            res[i++] = m.group();
        }
        return res;
    }

    // Return first percentage found in slice, or null
    private static String firstPercent(String slice) {
        if (slice == null) return null;
        Matcher m = Pattern.compile("[-+]?\\d+%").matcher(slice.replace(" ", ""));
        return m.find() ? m.group() : null;
    }

    // -------------------- Utilities --------------------
    private static String loadText(byte[] pdfBytes) throws IOException {
        // Detect DOCX by ZIP magic header 'PK\u0003\u0004'
        if (isDocx(pdfBytes)) {
            try (XWPFDocument docx = new XWPFDocument(new ByteArrayInputStream(pdfBytes));
                 XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
                return extractor.getText();
            }
        }
        // Detect PDF by '%PDF' header
        if (isPdf(pdfBytes)) {
            try (PDDocument doc = PDDocument.load(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                // Improve layout: keep reading order and token separation
                stripper.setSortByPosition(true);
                stripper.setWordSeparator(" ");
                stripper.setLineSeparator("\n");
                return stripper.getText(doc);
            }
        }
        // Else treat as plain text (e.g., .txt)
        return new String(pdfBytes, StandardCharsets.UTF_8);
    }

    private static boolean isDocx(byte[] bytes) {
        return bytes != null && bytes.length >= 4 && bytes[0] == 'P' && bytes[1] == 'K';
    }

    private static boolean isPdf(byte[] bytes) {
        return bytes != null && bytes.length >= 4 && bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F';
    }

    // Public helper to extract raw text (PDF/DOCX/TXT) for LLM usage
    public Mono<String> extractRawText(byte[] fileBytes) {
        return Mono.fromCallable(() -> loadText(fileBytes));
    }

    private static String norm(String s) {
        if (s == null) return "";
        // Uppercase, remove diacritics (rough), collapse whitespace
        String up = s.toUpperCase(Locale.ROOT)
                .replace('É', 'E')
                .replace('È', 'E')
                .replace('Ê', 'E')
                .replace('À', 'A')
                .replace('Â', 'A')
                .replace('Î', 'I')
                .replace('Ï', 'I')
                .replace('Ô', 'O')
                .replace('Û', 'U')
                .replace('Ü', 'U')
                .replace('Ç', 'C');
        return up.replace('\u00A0', ' ').replaceAll("\\s+", " ").trim();
    }

    private static boolean looksLikeTeamHeader(String line) {
        String n = norm(line);
        // Team header like: "CLUB OLYMPIQUE" possibly followed by other words
        return n.startsWith("CLUB OLYMPIQUE");
    }

    private static boolean looksLikeOtherTeamHeader(String line) {
        String n = norm(line);
        // Very rough: lines fully uppercase and long are likely headers
        return n.matches("[A-Z0-9 .-]{10,}") && (n.contains("CLUB") || n.contains("ES ") || n.contains("EST ") || n.contains("ESS "));
    }

    /**
     * Fuzzy match the teamFilter against a header line: tolerate abbreviations like
     * 'CLUB OLYMPIQUE KE' vs 'CLUB OLYMPIQUE KELIBIA'. Case/accents/extra spaces ignored.
     */
    private static boolean matchesTeamHeader(String line, String teamFilter) {
        String ln = norm(line);
        if (teamFilter == null || teamFilter.isBlank()) {
            return looksLikeTeamHeader(line);
        }
        String tf = norm(teamFilter);
        if (ln.contains(tf)) return true;
        // Token-based: require first two tokens to match in order
        String[] t = tf.split(" ");
        if (t.length >= 2) {
            String firstTwo = (t[0] + " " + t[1]).trim();
            if (ln.contains(firstTwo)) return true;
        }
        return false;
    }

    private static List<String> extractTeamBlock(List<String> lines, String teamFilter) {
        // Always target only COK: header starting with "CLUB OLYMPIQUE"
        int start = -1;
        for (int i = 0; i < lines.size(); i++) {
            String l = lines.get(i);
            if (looksLikeTeamHeader(l)) {
                start = i;
                break;
            }
        }
        if (start == -1) return Collections.emptyList();
        // collect until next team header or until stats header repeats (indicating next team table)
        List<String> block = new ArrayList<>();
        boolean statsHeaderSeen = false;
        int consecutiveBlank = 0;
        for (int i = start; i < Math.min(lines.size(), start + 120); i++) {
            String l = lines.get(i);
            String ln = norm(l);
            block.add(l);
            if (ln.isEmpty()) {
                consecutiveBlank++;
            } else {
                consecutiveBlank = 0;
            }
            // mark stats header presence
            if (!statsHeaderSeen && ln.contains("POINTS") && ln.contains("SERVICE") && (ln.contains("RECEPTION") || ln.contains("RÉCEPTION")) && (ln.contains("ATTAQUE") || ln.contains("ATTACK"))) {
                statsHeaderSeen = true;
            } else if (statsHeaderSeen && (ln.contains("POINTS") && ln.contains("SERVICE"))) {
                // next team table header detected
                break;
            }
            // break on other team header lines
            if (i > start + 2 && looksLikeOtherTeamHeader(l) && !looksLikeTeamHeader(l)) break;
            // if we already passed some rows and encounter a large blank gap, stop
            if (statsHeaderSeen && consecutiveBlank >= 3) break;
        }
        return block;
    }

    /**
     * Extract players from the COK team block. Heuristic based on: number [optional] + NAME + many spaces + columns.
     */
    public Mono<String[]> extractPlayers(byte[] pdfBytes, String teamFilter) {
        return Mono.fromCallable(() -> {
            String text = loadText(pdfBytes);
            List<String> lines = Arrays.asList(text.split("\r?\n"));
            List<String> block = extractTeamBlock(lines, teamFilter);
            if (block.isEmpty()) return new String[0];

            // Strictly capture the first cell: jersey number, optional role letter, then NAME in uppercase.
            // Stop name at two or more spaces or before numeric columns.
            Pattern row = Pattern.compile(
                    "^\\s*(?:\\d{1,2})\\s+(?:[A-Z]\\s+)?([A-Z][A-Z .'-]+?)(?=\\s{2,}|\\s+[0-9.])"
            );
            LinkedHashSet<String> names = new LinkedHashSet<>();
            // find the first stats header inside the block
            int headerIdx = -1;
            for (int i = 0; i < block.size(); i++) {
                String ln = norm(block.get(i));
                if (ln.contains("POINTS") && ln.contains("SERVICE") && (ln.contains("RECEPTION") || ln.contains("RÉCEPTION")) && (ln.contains("ATTAQUE") || ln.contains("ATTACK"))) {
                    headerIdx = i;
                    break;
                }
            }
            if (headerIdx == -1) headerIdx = 0; // fallback, but still limited to early rows

            int blanks = 0;
            for (int i = headerIdx + 1; i < block.size(); i++) {
                String l = block.get(i);
                String ln = norm(l);
                if (ln.isEmpty()) { blanks++; } else { blanks = 0; }
                // Stop at next table header or repeated stats header
                if (ln.contains("POINTS") && ln.contains("SERVICE")) break;
                if (looksLikeOtherTeamHeader(l) && !looksLikeTeamHeader(l)) break;
                if (blanks >= 3) break;

                // Skip separators
                if (ln.matches("[- .]{8,}")) continue;

                Matcher m = row.matcher(l.replace('\u00A0', ' '));
                if (m.find()) {
                    String name = norm(m.group(1));
                    if (name.length() >= 3 && !name.startsWith("CLUB OLYMPIQUE")) {
                        names.add(name);
                    }
                }
            }
            return names.toArray(new String[0]);
        });
    }

    /**
     * Extract stats for the selected player from the team block.
     * We try to parse Tot/Err for Reception; if not found, defaults to 0.
     */
    public Mono<String> extractPlayerStats(byte[] pdfBytes, String teamFilter, String playerName) {
        return Mono.fromCallable(() -> {
            String text = loadText(pdfBytes);
            List<String> lines = Arrays.asList(text.split("\r?\n"));
            List<String> block = extractTeamBlock(lines, teamFilter);

            String targetName = norm(playerName);
            int headerIdx = -1;
            for (int i = 0; i < block.size(); i++) {
                if (norm(block.get(i)).contains("RECEPTION") && norm(block.get(i)).contains("ATTACK")) {
                    headerIdx = i;
                    break;
                }
            }

            // Default totals
            int recTot = 0, recErr = 0, posPct = 0, excPct = 0;
            int attTot = 0, attKills = 0, attErr = 0, attBlk = 0, effPct = 0;
            int serveTot = 0, serveErr = 0, aces = 0;
            int points = 0, stuffs = 0;

            // Find player row
            for (int i = (headerIdx == -1 ? 0 : headerIdx + 1); i < block.size(); i++) {
                String l = block.get(i);
                String ln = norm(l);
                if (!ln.contains(" ")) continue;
                // weak match by containing targetName
                if (ln.contains(targetName)) {
                    // Split by 2+ spaces to get columns; first column is number+name
                    String clean = l.replace('\u00A0', ' ');
                    String[] parts = clean.trim().split("\\s{2,}");

                    // Gather numeric tokens from columns (excluding the first cell)
                    List<Integer> nums = new ArrayList<>();
                    for (int p = 1; p < parts.length; p++) {
                        String cell = parts[p].trim();
                        // Split further to catch tokens like "12%"
                        String[] tokens = cell.split("\\s+");
                        for (String tk : tokens) {
                            String tkn = tk.replace("%", "");
                            if (tkn.matches("-?\\d+")) {
                                try { nums.add(Integer.parseInt(tkn)); } catch (Exception ignored) {}
                            }
                        }
                    }

                    // Common layout mapping (best-effort):
                    // 0: points
                    // 1..3: serve tot, err, aces
                    // 4..7: reception tot, exc%, pos%, err
                    // 8..12: attack tot, kills, err, blk, eff%
                    // 13: block stuffs (if present) — else sometimes points already include blocks
                    if (nums.size() >= 1) points = nums.get(0);
                    if (nums.size() >= 2) serveTot = nums.get(1);
                    if (nums.size() >= 3) serveErr = nums.get(2);
                    if (nums.size() >= 4) aces = nums.get(3);

                    if (nums.size() >= 5) recTot = nums.get(4);
                    if (nums.size() >= 6) excPct = nums.get(5);
                    if (nums.size() >= 7) posPct = nums.get(6);
                    if (nums.size() >= 8) recErr = nums.get(7);

                    if (nums.size() >= 9)  attTot = nums.get(8);
                    if (nums.size() >= 10) attKills = nums.get(9);
                    if (nums.size() >= 11) attErr = nums.get(10);
                    if (nums.size() >= 12) attBlk = nums.get(11);
                    if (nums.size() >= 13) effPct = nums.get(12);

                    if (nums.size() >= 14) stuffs = nums.get(13);
                    break;
                }
            }

            String teamOut = teamFilter == null ? "CLUB OLYMPIQUE KELIBIA" : teamFilter;
            // Build minimal JSON with totals; bySet left empty for now
            String json = "{" +
                    "\n  \"team\": \"" + teamOut + "\"," +
                    "\n  \"player\": \"" + playerName + "\"," +
                    "\n  \"bySet\": {} ," +
                    "\n  \"total\": {" +
                    "\n    \"points\": " + points + "," +
                    "\n    \"serve\": {\n      \"aces\": " + aces + ", \n      \"err\": " + serveErr + ", \n      \"tot\": " + serveTot + "\n    }," +
                    "\n    \"reception\": {\n      \"tot\": " + recTot + ", \n      \"exc%\": " + excPct + ", \n      \"pos%\": " + posPct + ", \n      \"err\": " + recErr + "\n    }," +
                    "\n    \"attack\": {\n      \"tot\": " + attTot + ", \n      \"kills\": " + attKills + ", \n      \"err\": " + attErr + ", \n      \"blk\": " + attBlk + ", \n      \"eff%\": " + effPct + "\n    }," +
                    "\n    \"block\": {\n      \"stuffs\": " + stuffs + "\n    }" +
                    "\n  }" +
                    "\n}";
            return json;
        });
    }

    /**
     * Debug helper: returns team block raw lines and detected player names as JSON.
     */
    public Mono<String> debugTeamBlock(byte[] pdfBytes, String teamFilter) {
        return Mono.fromCallable(() -> {
                    String text = loadText(pdfBytes);
                    List<String> lines = Arrays.asList(text.split("\r?\n"));
                    return extractTeamBlock(lines, teamFilter);
                })
                .flatMap(block -> extractPlayers(pdfBytes, teamFilter)
                        .onErrorReturn(new String[0])
                        .map(names -> {
                            StringBuilder sb = new StringBuilder();
                            sb.append("{\n  \"lines\": [");
                            for (int i = 0; i < block.size(); i++) {
                                if (i > 0) sb.append(",");
                                sb.append("\n    ").append('"')
                                        .append(block.get(i).replace("\\", "\\\\").replace("\"", "\\\""))
                                        .append('"');
                            }
                            sb.append("\n  ],\n  \"detectedNames\": [");
                            for (int i = 0; i < names.length; i++) {
                                if (i > 0) sb.append(",");
                                sb.append("\n    ").append('"').append(names[i]).append('"');
                            }
                            sb.append("\n  ]\n}");
                            return sb.toString();
                        })
                );
    }
}
