package com.github.alexthe666.iceandfire.client.model.animator;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.util.IAFMath;
import net.minecraft.util.math.MathHelper;
import java.util.Iterator;
import java.util.Map;

public class IceAndFireTabulaModelAnimator {

    protected TabulaModel baseModel;

    public IceAndFireTabulaModelAnimator(TabulaModel baseModel) {
        this.baseModel = baseModel;
    }

    public void setRotateAngle(AdvancedModelBox model, float limbSwingAmount, float x, float y, float z) {
        model.rotateAngleX += limbSwingAmount * distance(model.rotateAngleX, x);
        model.rotateAngleY += limbSwingAmount * distance(model.rotateAngleY, y);
        model.rotateAngleZ += limbSwingAmount * distance(model.rotateAngleZ, z);
    }

    public void addToRotateAngle(AdvancedModelBox model, float limbSwingAmount, float x, float y, float z) {
        model.rotateAngleX += Math.min(limbSwingAmount * 2, 1) * distance(model.defaultRotationX, x);
        model.rotateAngleY += Math.min(limbSwingAmount * 2, 1) * distance(model.defaultRotationY, y);
        model.rotateAngleZ += Math.min(limbSwingAmount * 2, 1) * distance(model.defaultRotationZ, z);
    }

    public boolean isPartEqual(AdvancedModelBox original, AdvancedModelBox pose) {
        return pose != null && pose.rotateAngleX == original.defaultRotationX && pose.rotateAngleY == original.defaultRotationY && pose.rotateAngleZ == original.defaultRotationZ;
    }

    public boolean isPositionEqual(AdvancedModelBox original, AdvancedModelBox pose) {
        return pose.rotationPointX == original.defaultPositionX && pose.rotationPointY == original.defaultPositionY && pose.rotationPointZ == original.defaultPositionZ;
    }

    public void transitionTo(AdvancedModelBox from, AdvancedModelBox to, float timer, float maxTime, boolean oldFashioned) {
        if (oldFashioned) {
            from.rotateAngleX += ((to.rotateAngleX - from.rotateAngleX) / maxTime) * timer;
            from.rotateAngleY += ((to.rotateAngleY - from.rotateAngleY) / maxTime) * timer;
            from.rotateAngleZ += ((to.rotateAngleZ - from.rotateAngleZ) / maxTime) * timer;
        } else {
            transitionAngles(from, to, timer, maxTime);
        }
        from.rotationPointX += ((to.rotationPointX - from.rotationPointX) / maxTime) * timer;
        from.rotationPointY += ((to.rotationPointY - from.rotationPointY) / maxTime) * timer;
        from.rotationPointZ += ((to.rotationPointZ - from.rotationPointZ) / maxTime) * timer;

        //from.offsetX += ((to.offsetX - from.offsetX) / maxTime) * timer;
        //from.offsetY += ((to.offsetY - from.offsetY) / maxTime) * timer;
        //from.offsetZ += ((to.offsetZ - from.offsetZ) / maxTime) * timer;
    }

    public void transitionAngles(AdvancedModelBox from, AdvancedModelBox to, float timer, float maxTime) {
        from.rotateAngleX += ((distance(from.rotateAngleX, to.rotateAngleX)) / maxTime) * timer;
        from.rotateAngleY += ((distance(from.rotateAngleY, to.rotateAngleY)) / maxTime) * timer;
        from.rotateAngleZ += ((distance(from.rotateAngleZ, to.rotateAngleZ)) / maxTime) * timer;
    }

    public float distance(float rotateAngleFrom, float rotateAngleTo) {
        return (float) IAFMath.atan2_accurate(MathHelper.sin(rotateAngleTo - rotateAngleFrom), MathHelper.cos(rotateAngleTo - rotateAngleFrom));
    }

    public void rotate(ModelAnimator animator, AdvancedModelBox model, float x, float y, float z) {
        animator.rotate(model, (float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
    }

    public void moveToPoseSameModel(TabulaModel model, TabulaModel modelTo){
        Map<String, AdvancedModelBox> modelMap = model.getCubes();
        Map<String, AdvancedModelBox> modelToMap = modelTo.getCubes();
        Map<String, AdvancedModelBox> baseModelMap = baseModel.getCubes();
        //Just in case check if the sizes are the same
        if (modelMap.size() == modelToMap.size() && modelToMap.size() == baseModelMap.size()) {
            Iterator<Map.Entry<String, AdvancedModelBox>> modelIter = modelMap.entrySet().iterator();
            Iterator<Map.Entry<String, AdvancedModelBox>> modelToIter = modelToMap.entrySet().iterator();
            Iterator<Map.Entry<String, AdvancedModelBox>> baseModelIter = baseModelMap.entrySet().iterator();
            while (modelIter.hasNext()) {
                AdvancedModelBox cube = modelIter.next().getValue();
                AdvancedModelBox modelToCube = modelToIter.next().getValue();
                AdvancedModelBox baseCube = baseModelIter.next().getValue();
                if (!isPartEqual(baseCube, modelToCube)) {
                    float toX = modelToCube.rotateAngleX;
                    float toY = modelToCube.rotateAngleY;
                    float toZ = modelToCube.rotateAngleZ;
                    model.llibAnimator.rotate(cube, distance(cube.rotateAngleX, toX), distance(cube.rotateAngleY, toY), distance(cube.rotateAngleZ, toZ));
                }
                if (!isPositionEqual(baseCube, modelToCube)) {
                    float toX = modelToCube.rotationPointX;
                    float toY = modelToCube.rotationPointY;
                    float toZ = modelToCube.rotationPointZ;
                    model.llibAnimator.move(cube, distance(cube.rotationPointX, toX), distance(cube.rotationPointY, toY), distance(cube.rotationPointZ, toZ));
                }
            }
        }
        else {
            //fallback function
            moveToPose(model,modelTo);
        }
    }
    public void moveToPose(TabulaModel model, TabulaModel modelTo) {
        for (AdvancedModelBox cube : model.getCubes().values()) {
            AdvancedModelBox cubeTo = modelTo.getCube(cube.boxName);
            if (!isPartEqual(baseModel.getCube(cube.boxName), cubeTo)) {
                float toX = cubeTo.rotateAngleX;
                float toY = cubeTo.rotateAngleY;
                float toZ = cubeTo.rotateAngleZ;
                model.llibAnimator.rotate(cube, distance(cube.rotateAngleX, toX), distance(cube.rotateAngleY, toY), distance(cube.rotateAngleZ, toZ));
            }
            if (!isPositionEqual(baseModel.getCube(cube.boxName), cubeTo)) {
                float toX = cubeTo.rotationPointX;
                float toY = cubeTo.rotationPointY;
                float toZ = cubeTo.rotationPointZ;
                model.llibAnimator.move(cube, distance(cube.rotationPointX, toX), distance(cube.rotationPointY, toY), distance(cube.rotationPointZ, toZ));
            }
        }
    }
}
