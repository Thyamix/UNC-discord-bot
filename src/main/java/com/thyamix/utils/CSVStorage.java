package com.thyamix.utils;

import com.thyamix.enums.StoredType;

import java.io.*;
import java.util.Optional;

public class CSVStorage {
    private final File file;

    public CSVStorage(String taskName) {
        this.file = new File(taskName);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                writer.println("type,userId,userName,timestamp");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addEntry(StoredType type, String userId, String userName, long timestamp) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            writer.printf("%s,%s,%s,%d%n", type, userId, userName, timestamp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<String[]> getLastEntry() {
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file)) {
            String lastLine;
            while ((lastLine = reader.readLine()) != null) {
                if (lastLine.isBlank()) {
                    continue;
                }

                String[] parts = lastLine.split(",");
                if (parts.length < 4) {
                    continue;
                }

                if ("timestamp".equals(parts[3])) {
                    continue;
                }

                try {
                    Long.parseLong(parts[3]);
                } catch (NumberFormatException ex) {
                    continue;
                }

                return Optional.of(parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    static class ReversedLinesFileReader implements Closeable {
        private final RandomAccessFile raf;
        private long position;

        public ReversedLinesFileReader(File file) throws IOException {
            this.raf = new RandomAccessFile(file, "r");
            this.position = raf.length();
        }

        public String readLine() throws IOException {
            if (position <= 0) return null;

            StringBuilder sb = new StringBuilder();
            while (--position >= 0) {
                raf.seek(position);
                int c = raf.read();
                if (c == '\n' && sb.length() > 0) break;
                sb.insert(0, (char) c);
            }
            return sb.toString().trim();
        }

        @Override
        public void close() throws IOException {
            raf.close();
        }
    }
}
