package mars.mips.instructions.customlangs;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.syscalls.*;
import mars.*;
import mars.util.*;
import java.util.*;
import java.io.*;
import mars.mips.instructions.*;
import java.util.Random;

public class MagicAssembly extends CustomAssembly {
    @Override
    public String getName() {
        return "Magic Assembly";
    }

    @Override
    public String getDescription() {
        return "Assembly language themed after the mystical arts";
    }

    @Override
    protected void populate() {
        instructionList.add(
                new BasicInstruction("fuse $t0,$t1,$t2",
                        "Fuse: rd = rs + rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 ddddd sssss ttttt 00000 000001",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1], rt = operands[2];
                                int result = RegisterFile.getValue(rs) + RegisterFile.getValue(rt);
                                RegisterFile.updateRegister(rd, result);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("dspl $t0,$t1,$t2",
                        "Dispel: rd = rs - rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 ddddd sssss ttttt 00000 000010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1], rt = operands[2];
                                int result = RegisterFile.getValue(rs) - RegisterFile.getValue(rt);
                                RegisterFile.updateRegister(rd, result);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("alchm $t0,$t1,$t2",
                        "Alchemize: rd = rs * rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 ddddd sssss ttttt 00000 000011",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1], rt = operands[2];
                                int result = RegisterFile.getValue(rs) * RegisterFile.getValue(rt);
                                RegisterFile.updateRegister(rd, result);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("dspt $t0,$t1,$t2",
                        "Dissipate: rd = rs / rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 ddddd sssss ttttt 00000 000100",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1], rt = operands[2];
                                int divisor = RegisterFile.getValue(rt);
                                if (divisor == 0) {
                                    throw new ProcessingException(statement, "Division by zero");
                                }
                                int result = RegisterFile.getValue(rs) / divisor;
                                RegisterFile.updateRegister(rd, result);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("bind $t0,$t1,$t2",
                        "Bind: rd = rs & rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 ddddd sssss ttttt 00000 000101",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1], rt = operands[2];
                                RegisterFile.updateRegister(rd, RegisterFile.getValue(rs) & RegisterFile.getValue(rt));
                            }
                        }));

        instructionList.add(
                new BasicInstruction("svr $t0,$t1,$t2",
                        "Sever: rd = rs | rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 ddddd sssss ttttt 00000 000110",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1], rt = operands[2];
                                RegisterFile.updateRegister(rd, RegisterFile.getValue(rs) | RegisterFile.getValue(rt));
                            }
                        }));

        instructionList.add(
                new BasicInstruction("veil $t0,$t1,$t2",
                        "Veil: rd = rs ^ rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 ddddd sssss ttttt 00000 000111",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1], rt = operands[2];
                                RegisterFile.updateRegister(rd, RegisterFile.getValue(rs) ^ RegisterFile.getValue(rt));
                            }
                        }));

        instructionList.add(
                new BasicInstruction("cnjr $t0,4($t1)",
                        "Conjure: R[rt] = Memory[R[rs] + imm]",
                        BasicInstructionFormat.I_FORMAT,
                        "100000 rrrrr sssss immediate",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rt = operands[0], rs = operands[1];
                                int imm = operands[2] << 16 >> 16;
                                int byteAddress = RegisterFile.getValue(rs) + imm;
                                try {
                                    int word = Globals.memory.getWord(byteAddress);
                                    RegisterFile.updateRegister(rt, word);
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("implant $t0,4($t1)",
                        "Implant: Memory[R[rs] + imm] = R[rt]",
                        BasicInstructionFormat.I_FORMAT,
                        "100001 rrrrr sssss immediate",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rt = operands[0], rs = operands[1];
                                int imm = operands[2] << 16 >> 16;
                                int byteAddress = RegisterFile.getValue(rs) + imm;
                                try {
                                    Globals.memory.setWord(byteAddress, RegisterFile.getValue(rt));
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("chnl $t0,$t1,100",
                        "Channel: R[rd] = R[rs] + immediate",
                        BasicInstructionFormat.I_FORMAT,
                        "011000 rrrrr sssss immediate",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1];
                                int imm = operands[2] << 16 >> 16;
                                RegisterFile.updateRegister(rd, RegisterFile.getValue(rs) + imm);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("amp $t0,$t1,100",
                        "Amplify: R[rd] = R[rs] * immediate",
                        BasicInstructionFormat.I_FORMAT,
                        "101000 rrrrr sssss immediate",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1];
                                int imm = operands[2] << 16 >> 16;
                                RegisterFile.updateRegister(rd, RegisterFile.getValue(rs) * imm);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("cncl $t0",
                        "Cancel: rd = 0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 ddddd 00000 00000 00000 010010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int rd = statement.getOperands()[0];
                                RegisterFile.updateRegister(rd, 0);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("ward $t0,$t1,label",
                        "Ward: branch if R[rs] == R[rt] to label",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000100 sssss ttttt branchoffset",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rs = operands[0], rt = operands[1], branchTarget = operands[2];
                                if (RegisterFile.getValue(rs) == RegisterFile.getValue(rt)) {
                                    Globals.instructionSet.processBranch(branchTarget);
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("curse $t0,$t1,label",
                        "Curse: branch if R[rs] != R[rt] to label",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000101 sssss ttttt branchoffset",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rs = operands[0], rt = operands[1], branchTarget = operands[2];
                                if (RegisterFile.getValue(rs) != RegisterFile.getValue(rt)) {
                                    Globals.instructionSet.processBranch(branchTarget);
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("tp label",
                        "Teleport: unconditional jump to label",
                        BasicInstructionFormat.J_FORMAT,
                        "000010 address26",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                String label = statement.getOriginalTokenList().size() > 1
                                        ? statement.getOriginalTokenList().get(1).getValue()
                                        : null;
                                if (label == null) {
                                    throw new ProcessingException(statement, "Missing label for tp");
                                }
                                int byteAddress = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);
                                Globals.instructionSet.processBranch(byteAddress);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("trns $t0,$t1",
                        "Transmute: R[rd] = R[rs] (copy)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 ddddd sssss 00000 00000 010000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1];
                                RegisterFile.updateRegister(rd, RegisterFile.getValue(rs));
                            }
                        }));

        instructionList.add(
                new BasicInstruction("swap $t0,$t1",
                        "Swap: exchange contents of rs and rt",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 sssss ttttt 00000 010001",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rs = operands[0], rt = operands[1];
                                int tmp = RegisterFile.getValue(rs);
                                RegisterFile.updateRegister(rs, RegisterFile.getValue(rt));
                                RegisterFile.updateRegister(rt, tmp);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("clone $t0,$t1",
                        "Clone: R[rd] = R[rs] + R[rs]",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 ddddd sssss 00000 00000 010011",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1];
                                int val = RegisterFile.getValue(rs);
                                RegisterFile.updateRegister(rd, val + val);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("brir $t0,$t1",
                        "Barrier: R[rd] = max(R[rs],0) (clamp negative to zero)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 ddddd sssss 00000 00000 010100",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd = operands[0], rs = operands[1];
                                int val = RegisterFile.getValue(rs);
                                if (val < 0) val = 0;
                                RegisterFile.updateRegister(rd, val);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("chrg $t0",
                        "Charge: increment R[rs] by 1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 sssss 00000 00000 010101",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rs = operands[0];
                                int val = RegisterFile.getValue(rs) + 1;
                                RegisterFile.updateRegister(rs, val);
                            }
                        }));
    }
}
