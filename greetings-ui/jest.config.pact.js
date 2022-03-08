module.exports = {
  preset: "jest-preset-angular",
  testMatch: ["**/+(*.)+(spec).(pact).(ts)"],
  testURL: "http://localhost:54546",
  setupFilesAfterEnv: ["<rootDir>/setup-jest.ts"],
};
