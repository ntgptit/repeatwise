import{j as s,s as i,a8 as r}from"./index-Cg5IDLAj.js";const e=i("span")(({theme:o})=>`
      background-color: ${o.colors.alpha.black[5]};
      padding: ${o.spacing(.5,1)};
      font-size: ${o.typography.pxToRem(13)};
      border-radius: ${o.general.borderRadius};
      display: inline-flex;
      align-items: center;
      justify-content: center;
      max-height: ${o.spacing(3)};
      
      &.MuiLabel {
        &-primary {
          background-color: ${o.colors.primary.lighter};
          color: ${o.palette.primary.main}
        }

        &-black {
          background-color: ${o.colors.alpha.black[100]};
          color: ${o.colors.alpha.white[100]};
        }
        
        &-secondary {
          background-color: ${o.colors.secondary.lighter};
          color: ${o.palette.secondary.main}
        }
        
        &-success {
          background-color: ${o.colors.success.lighter};
          color: ${o.palette.success.main}
        }
        
        &-warning {
          background-color: ${o.colors.warning.lighter};
          color: ${o.palette.warning.main}
        }
              
        &-error {
          background-color: ${o.colors.error.lighter};
          color: ${o.palette.error.main}
        }
        
        &-info {
          background-color: ${o.colors.info.lighter};
          color: ${o.palette.info.main}
        }
      }
`),p=({className:o,color:a="secondary",children:c,...l})=>{const n=["MuiLabel",`MuiLabel-${a}`,o].filter(Boolean).join(" ");return s.jsx(e,{className:n,...l,children:c})};p.propTypes={children:r.node,className:r.string,color:r.oneOf(["primary","black","secondary","error","warning","success","info"])};export{p as L};
