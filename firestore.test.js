const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read users", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can read and write own user document", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      nama: "Alice",
      createdAt: new Date(),
      lastActive: new Date(),
      skorRealitasTerakhir: 75,
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read or write another user document", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).set({
      nama: "Hacker Bob",
      createdAt: new Date(),
      lastActive: new Date(),
      skorRealitasTerakhir: 0,
    })
  );
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create and query own session", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("sessions").doc("session_1").set({
      uid: ALICE_UID,
      createdAt: new Date(),
      ceritaPengguna: "Saya merasa bingung menentukan prioritas karier.",
      diagnosis: "Kebingungan timbul dari disparitas ekspektasi dan kapabilitas saat ini.",
      petaRealitas: {
        keuangan: 70,
        keluarga: 80,
        karier: 50,
        relasi: 65,
        tujuan: 60,
        risiko: 40,
        psikologi: 55,
        kesehatan: 75,
      },
      analisisAkarMasalah: [
        "Kurangnya batasan waktu kerja",
        "Ekspektasi tidak terkomunikasikan",
        "Kelelahan kognitif harian",
        "Penundaan keputusan penting",
      ],
      masalahUtama: "Fragmentasi fokus yang menghambat momentum karier.",
      tingkatRisiko: 45,
      tingkatKepercayaanAI: 88,
      skenario: [
        { nama: "Stabil", deskripsi: "Fokus konsolidasi posisi", probabilitas: 70, risiko: 20, dampak: 40 },
        { nama: "Berani", deskripsi: "Negosiasi tanggung jawab baru", probabilitas: 55, risiko: 45, dampak: 70 },
        { nama: "Agresif", deskripsi: "Transisi jalur karier", probabilitas: 35, risiko: 75, dampak: 90 },
      ],
      rekomendasiSkenario: "Berani",
      zebra: {
        instruksi: "Tuliskan 3 pencapaian kuantitatif kuartal ini sebelum jam 10 pagi.",
        dampakPoin: 12,
        status: "pending",
      },
    })
  );

  await assertSucceeds(
    aliceDb.collection("sessions").where("uid", "==", ALICE_UID).get()
  );
});

test("Another user: cannot query Alice's sessions", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("sessions").doc("session_1").get()
  );
});
