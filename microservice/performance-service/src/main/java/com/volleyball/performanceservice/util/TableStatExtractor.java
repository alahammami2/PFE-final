package com.volleyball.performanceservice.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Heuristic parser for COK match report tables extracted via PDFBox text.
 * It anchors to the table header and the player's row, then slices the row
 * by header label positions and extracts numbers per section.
 */
public class TableStatExtractor {

    public static class Stats {
        public String vote = "Non disponible";
        public String totPoints = "Non disponible";
        public String wl = "Non disponible";
        public String svcTot = "Non disponible";
        public String svcErr = "Non disponible";
        public String svcPts = "Non disponible";
        public String recTot = "Non disponible";
        public String recErr = "Non disponible";
        public String atkTot = "Non disponible";
        public String atkErr = "Non disponible";
        public String bkPts = "Non disponible";

        public String formatSchema() {
            StringBuilder out = new StringBuilder();
            out.append("Informations générales :\n");
            out.append("- Vote : ").append(vote).append('\n');
            out.append("- Points totaux (Tot) : ").append(totPoints).append('\n');
            out.append("- Ratio Victoire–Défaite (W–L) : ").append(wl).append('\n');
            out.append('\n');
            out.append("Service :\n");
            out.append("- Total services : ").append(svcTot).append('\n');
            out.append("- Erreurs service : ").append(svcErr).append('\n');
            out.append("- Points service (Aces) : ").append(svcPts).append('\n');
            out.append('\n');
            out.append("Réception :\n");
            out.append("- Total réceptions : ").append(recTot).append('\n');
            out.append("- Erreurs réception : ").append(recErr).append('\n');
            out.append('\n');
            out.append("Attaque :\n");
            out.append("- Total attaques : ").append(atkTot).append('\n');
            out.append("- Erreurs attaque : ").append(atkErr).append('\n');
            out.append('\n');
            out.append("Bloc :\n");
            out.append("- Points de blocage (BK) : ").append(bkPts);
            return out.toString();
        }
    }

    public static Stats tryExtract(String rawText, String question) {
        if (rawText == null || rawText.isBlank() || question == null || question.isBlank()) return null;
        String[] lines = rawText.replace("\r", "").split("\n");
        int headerIdx = findHeaderIndex(lines);
        if (headerIdx < 0) return null;

        String[] nameTokens = guessNameTokens(question);
        if (nameTokens == null) return null;
        int playerIdx = findPlayerIndex(lines, nameTokens);
        if (playerIdx < 0) return null;

        String header = lines[headerIdx];
        String row = lines[playerIdx];
        // Some PDFs wrap the numeric cells to the next line; if the next line has few letters,
        // consider it a continuation of the player's row.
        if (playerIdx + 1 < lines.length) {
            String next = lines[playerIdx + 1];
            int letters = next.replaceAll("[^A-Za-zÀ-ÿ]", "").length();
            int digits = next.replaceAll("[^0-9]", "").length();
            if (digits > 0 && letters <= 2) {
                row = row + " " + next.trim();
            }
        }

        int colPoints = positionOfAny(header, new String[]{"points","point","pts","pt","score","tot"});
        int colServe = positionOfAny(header, new String[]{"serve","service"});
        int colReception = positionOfAny(header, new String[]{"reception","réception","recv"});
        int colAttack = positionOfAny(header, new String[]{"attack","attaque","att"});
        int colBK = positionOfAny(header, new String[]{"bk","bloc","block","blk"});

        if (colPoints < 0 || colServe < 0 || colReception < 0 || colAttack < 0 || colBK < 0) {
            return null;
        }

        String segPoints = safeSub(row, colPoints, colServe);
        String segServe = safeSub(row, colServe, colReception);
        String segReception = safeSub(row, colReception, colAttack);
        String segAttack = safeSub(row, colAttack, colBK);
        String segBK = safeSub(row, colBK, row.length());

        Stats s = new Stats();
        // Points segment: Vote, Tot, W-L
        List<String> numsPoints = numbersInOrder(segPoints, true);
        if (!numsPoints.isEmpty()) s.vote = numsPoints.get(0);
        if (numsPoints.size() >= 2) s.totPoints = numsPoints.get(1);
        String wl = extractWL(segPoints);
        if (wl != null) s.wl = wl;

        // Serve: Tot, Err, Pts
        List<String> numsServe = numbersInOrder(segServe, false);
        if (numsServe.size() >= 1) s.svcTot = numsServe.get(0);
        if (numsServe.size() >= 2) s.svcErr = numsServe.get(1);
        if (numsServe.size() >= 3) s.svcPts = numsServe.get(2);

        // Reception: Tot, Err
        List<String> numsRec = numbersInOrder(segReception, false);
        if (numsRec.size() >= 1) s.recTot = numsRec.get(0);
        if (numsRec.size() >= 2) s.recErr = numsRec.get(1);

        // Attack: Tot, Err
        List<String> numsAtk = numbersInOrder(segAttack, false);
        if (numsAtk.size() >= 1) s.atkTot = numsAtk.get(0);
        if (numsAtk.size() >= 2) s.atkErr = numsAtk.get(1);

        // BK: Pts
        List<String> numsBk = numbersInOrder(segBK, false);
        if (numsBk.size() >= 1) s.bkPts = numsBk.get(0);

        return s;
    }

    private static int findHeaderIndex(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            String L = lines[i].toLowerCase(Locale.ROOT);
            int hit = 0;
            if (containsAny(L, new String[]{"points","point","pts","pt","score","tot"})) hit++;
            if (containsAny(L, new String[]{"serve","service"})) hit++;
            if (containsAny(L, new String[]{"reception","réception","recv"})) hit++;
            if (containsAny(L, new String[]{"attack","attaque","att"})) hit++;
            if (containsAny(L, new String[]{"bk","bloc","block","blk"})) hit++;
            if (hit >= 4) return i; // suffisamment de colonnes sur la ligne
            // Tenter une détection sur 2 lignes (en-tête sur 2 lignes)
            if (i + 1 < lines.length) {
                String L2 = (L + " " + lines[i+1].toLowerCase(Locale.ROOT));
                hit = 0;
                if (containsAny(L2, new String[]{"points","point","pts","pt","score","tot"})) hit++;
                if (containsAny(L2, new String[]{"serve","service"})) hit++;
                if (containsAny(L2, new String[]{"reception","réception","recv"})) hit++;
                if (containsAny(L2, new String[]{"attack","attaque","att"})) hit++;
                if (containsAny(L2, new String[]{"bk","bloc","block","blk"})) hit++;
                if (hit >= 4) return i;
            }
        }
        return -1;
    }

    private static String[] guessNameTokens(String question) {
        var m = java.util.regex.Pattern.compile("([A-Za-zÀ-ÿ]{2,})\\s+([A-Za-zÀ-ÿ]{2,})").matcher(question);
        if (m.find()) return new String[]{m.group(1), m.group(2)};
        return null;
    }

    private static int findPlayerIndex(String[] lines, String[] name) {
        String first = normalize(name[0]);
        String last = normalize(name[1]);
        String fi = first.isEmpty() ? "" : first.substring(0,1);
        for (int i = 0; i < lines.length; i++) {
            String L = normalize(lines[i]);
            if (L.contains(first + " " + last) || L.contains(last + " " + first)) return i;
            if (!fi.isEmpty()) {
                if (L.contains(last + " " + fi) || L.contains(fi + ". " + last) || L.contains(last + " " + fi + ".")) return i;
            }
            if (L.contains(last)) return i;
        }
        return -1;
    }

    private static String normalize(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return n.toLowerCase(Locale.ROOT);
    }

    private static boolean containsAny(String s, String[] needles) {
        String low = s.toLowerCase(Locale.ROOT);
        for (String n : needles) if (low.contains(n.toLowerCase(Locale.ROOT))) return true;
        return false;
    }

    private static int positionOfAny(String s, String[] needles) {
        String low = s.toLowerCase(Locale.ROOT);
        int best = -1;
        for (String n : needles) {
            int idx = low.indexOf(n.toLowerCase(Locale.ROOT));
            if (idx >= 0 && (best < 0 || idx < best)) best = idx;
        }
        return best;
    }

    private static String safeSub(String s, int from, int to) {
        int a = Math.max(0, Math.min(from, s.length()));
        int b = Math.max(a, Math.min(to, s.length()));
        return s.substring(a, b);
    }

    private static List<String> numbersInOrder(String s, boolean allowDecimal) {
        List<String> out = new ArrayList<>();
        String pattern = allowDecimal ? "-?\\d+(?:[\\.,]\\d+)?" : "-?\\d+";
        var m = java.util.regex.Pattern.compile(pattern).matcher(s);
        while (m.find()) out.add(m.group().replace(',', '.'));
        return out;
    }

    private static String extractWL(String s) {
        // Accept patterns like "+2" or "+2 -1" or "2-1"
        var m = java.util.regex.Pattern.compile("([+\u2212-]?\\d+\\s*[–—-]\\s*[+\u2212-]?\\d+)").matcher(s);
        if (m.find()) return m.group(1).replace('\u2212', '-');
        // fallback: first signed number with plus/minus
        m = java.util.regex.Pattern.compile("[+\u2212-]?\\d+").matcher(s);
        if (m.find()) return m.group();
        return null;
    }
}
